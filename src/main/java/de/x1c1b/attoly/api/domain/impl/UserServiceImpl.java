package de.x1c1b.attoly.api.domain.impl;

import de.x1c1b.attoly.api.domain.UserService;
import de.x1c1b.attoly.api.domain.UserVerificationService;
import de.x1c1b.attoly.api.domain.exception.EmailAlreadyInUseException;
import de.x1c1b.attoly.api.domain.exception.EntityNotFoundException;
import de.x1c1b.attoly.api.domain.exception.MustBeAdministrableException;
import de.x1c1b.attoly.api.domain.model.Role;
import de.x1c1b.attoly.api.domain.model.RoleName;
import de.x1c1b.attoly.api.domain.model.User;
import de.x1c1b.attoly.api.domain.payload.UserCreationPayload;
import de.x1c1b.attoly.api.domain.payload.UserUpdatePayload;
import de.x1c1b.attoly.api.repository.RoleRepository;
import de.x1c1b.attoly.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserVerificationService userVerificationService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           UserVerificationService userVerificationService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userVerificationService = userVerificationService;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public User findById(UUID id) throws EntityNotFoundException {
        return userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public User findByEmail(String email) throws EntityNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public boolean existsById(UUID id) {
        return userRepository.existsById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public long count() {
        return userRepository.count();
    }

    @Override
    @Transactional
    public User create(UserCreationPayload payload) throws EmailAlreadyInUseException {
        if (userRepository.existsAnyByEmail(payload.getEmail())) {
            throw new EmailAlreadyInUseException(payload.getEmail());
        }

        User user = User.builder()
                .email(payload.getEmail())
                .password(passwordEncoder.encode(payload.getPassword()))
                .roles(Set.of(roleRepository.findByName(RoleName.ROLE_USER).orElseThrow()))
                .build();

        User newUser = userRepository.save(user);
        userVerificationService.sendVerificationMessageById(newUser.getId());

        return newUser;
    }

    @Override
    @Transactional
    public User updateById(UUID id, UserUpdatePayload payload) throws EntityNotFoundException {
        return update(findById(id), payload);
    }

    @Override
    @Transactional
    public User updateByEmail(String email, UserUpdatePayload payload) throws EntityNotFoundException {
        return update(findByEmail(email), payload);
    }

    protected User update(User user, UserUpdatePayload payload) {
        if (payload.getPassword().isPresent()) {
            user.setPassword(passwordEncoder.encode(payload.getPassword().get()));
        }

        return userRepository.save(user);
    }

    @Override
    public void assignRoleById(UUID id, UUID role) throws EntityNotFoundException {
        assignsRole(findById(id), role);
    }

    @Override
    public void assignRoleByEmail(String email, UUID role) throws EntityNotFoundException {
        assignsRole(findByEmail(email), role);
    }

    @Transactional
    protected void assignsRole(User user, UUID roleId) throws EntityNotFoundException {
        Role role = roleRepository.findById(roleId).orElseThrow(EntityNotFoundException::new);

        user.getRoles().add(role);
        userRepository.save(user);
    }

    @Override
    public void removeRoleById(UUID userId, UUID roleId) throws EntityNotFoundException {
        removeRole(findById(userId), roleId);
    }

    @Override
    public void removeRoleByEmail(String email, UUID roleId) throws EntityNotFoundException {
        removeRole(findByEmail(email), roleId);
    }

    @Transactional
    protected void removeRole(User user, UUID roleId) throws EntityNotFoundException {
        Role role = roleRepository.findById(roleId).orElseThrow(EntityNotFoundException::new);
        boolean affectsAdministration = role.getName().equals(RoleName.ROLE_ADMIN);

        if (affectsAdministration && !userRepository.existsAnyWithRoleExceptEmail(RoleName.ROLE_ADMIN, user.getEmail())) {
            throw new MustBeAdministrableException();
        }

        user.getRoles().remove(role);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) throws EntityNotFoundException {
        delete(findById(id));
    }

    @Override
    @Transactional
    public void deleteByEmail(String email) throws EntityNotFoundException {
        delete(findByEmail(email));
    }

    protected void delete(User user) {
        boolean isAdministrator = user.getRoles().stream().anyMatch(role -> role.getName().equals(RoleName.ROLE_ADMIN));

        if (isAdministrator && !userRepository.existsAnyWithRoleExceptEmail(RoleName.ROLE_ADMIN, user.getEmail())) {
            throw new MustBeAdministrableException();
        }

        userRepository.deleteSoft(user);
    }
}
