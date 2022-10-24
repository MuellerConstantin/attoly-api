package de.x1c1b.attoly.api.domain.impl;

import de.x1c1b.attoly.api.domain.EmailAlreadyInUseException;
import de.x1c1b.attoly.api.domain.EntityNotFoundException;
import de.x1c1b.attoly.api.domain.UserService;
import de.x1c1b.attoly.api.domain.model.Role;
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

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
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
    public User create(UserCreationPayload payload) throws EmailAlreadyInUseException {
        if (userRepository.existsAnyByEmail(payload.getEmail())) {
            throw new EmailAlreadyInUseException(payload.getEmail());
        }

        User user = User.builder()
                .email(payload.getEmail())
                .password(passwordEncoder.encode(payload.getPassword()))
                .build();

        return userRepository.save(user);
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
    @Transactional
    public User assignRolesById(UUID id, String... roleNames) throws EntityNotFoundException, IllegalArgumentException {
        return assignRoles(findById(id), roleNames);
    }

    @Override
    @Transactional
    public User assignRolesByEmail(String email, String... roleNames) throws EntityNotFoundException, IllegalArgumentException {
        return assignRoles(findByEmail(email), roleNames);
    }

    @Transactional
    protected User assignRoles(User user, String... roleNames) throws IllegalArgumentException {
        Set<Role> roles = Arrays.stream(roleNames)
                .map((roleName) -> roleRepository.findByName(roleName).orElseThrow(IllegalArgumentException::new))
                .collect(Collectors.toSet());

        user.getRoles().addAll(roles);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User removeRoleById(UUID id, String roleName) throws EntityNotFoundException, IllegalArgumentException {
        return removeRole(findById(id), roleName);
    }

    @Override
    @Transactional
    public User removeRoleByEmail(String email, String roleName) throws EntityNotFoundException, IllegalArgumentException {
        return removeRole(findByEmail(email), roleName);
    }

    @Transactional
    protected User removeRole(User user, String roleName) throws IllegalArgumentException {
        Role role = roleRepository.findByName(roleName).orElseThrow(IllegalArgumentException::new);

        user.getRoles().remove(role);
        return userRepository.save(user);
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
        userRepository.deleteSoft(user);
    }
}
