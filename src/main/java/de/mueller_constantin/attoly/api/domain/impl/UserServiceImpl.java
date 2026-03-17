package de.mueller_constantin.attoly.api.domain.impl;

import de.mueller_constantin.attoly.api.domain.DisposableEmailDomainService;
import de.mueller_constantin.attoly.api.domain.UserService;
import de.mueller_constantin.attoly.api.domain.UserVerificationService;
import de.mueller_constantin.attoly.api.domain.exception.*;
import de.mueller_constantin.attoly.api.domain.result.UserResult;
import de.mueller_constantin.attoly.api.domain.result.mapper.UserResultMapper;
import de.mueller_constantin.attoly.api.repository.model.Role;
import de.mueller_constantin.attoly.api.repository.model.RoleName;
import de.mueller_constantin.attoly.api.repository.model.SubscriptionStatus;
import de.mueller_constantin.attoly.api.repository.model.User;
import de.mueller_constantin.attoly.api.domain.payload.UserCreationPayload;
import de.mueller_constantin.attoly.api.domain.payload.UserUpdatePayload;
import de.mueller_constantin.attoly.api.repository.RoleRepository;
import de.mueller_constantin.attoly.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserVerificationService userVerificationService;
    private final DisposableEmailDomainService disposableEmailDomainService;
    private final UserResultMapper userResultMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           UserVerificationService userVerificationService,
                           DisposableEmailDomainService disposableEmailDomainService,
                           UserResultMapper userResultMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userVerificationService = userVerificationService;
        this.disposableEmailDomainService = disposableEmailDomainService;
        this.userResultMapper = userResultMapper;
    }

    @Override
    public List<UserResult> findAll() {
        var users = userRepository.findAll();
        return userResultMapper.mapToResult(users);
    }

    @Override
    public Page<UserResult> findAll(Pageable pageable) {
        var users = userRepository.findAll(pageable);
        return userResultMapper.mapToResult(users);
    }

    @Override
    public Page<UserResult> findAll(Specification<User> specification, Pageable pageable) {
        specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("deleted"), false));
        var users = userRepository.findAll(specification, pageable);
        return userResultMapper.mapToResult(users);
    }

    @Override
    public UserResult findById(UUID id) throws EntityNotFoundException {
        var user = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return userResultMapper.mapToResult(user);
    }

    @Override
    public UserResult findByEmail(String email) throws EntityNotFoundException {
        var user = userRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
        return userResultMapper.mapToResult(user);
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
    public UserResult create(UserCreationPayload payload) throws EmailAlreadyInUseException {
        if (userRepository.existsAnyByEmail(payload.getEmail())) {
            throw new EmailAlreadyInUseException(payload.getEmail());
        }

        if (disposableEmailDomainService.isDisposable(payload.getEmail())) {
            throw new EmailNotAllowedException(payload.getEmail());
        }

        User user = User.builder()
                .email(payload.getEmail())
                .password(passwordEncoder.encode(payload.getPassword()))
                .roles(Set.of(roleRepository.findByName(RoleName.ROLE_USER).orElseThrow()))
                .build();

        Locale locale = LocaleContextHolder.getLocale();

        User newUser = userRepository.save(user);
        userVerificationService.sendVerificationMessageById(newUser.getId(), locale);

        return userResultMapper.mapToResult(newUser);
    }

    @Override
    @Transactional
    public UserResult updateById(UUID id, UserUpdatePayload payload) throws EntityNotFoundException {
        var user = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return update(user, payload);
    }

    @Override
    @Transactional
    public UserResult updateByEmail(String email, UserUpdatePayload payload) throws EntityNotFoundException {
        var user = userRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
        return update(user, payload);
    }

    @Override
    @Transactional
    public void changePasswordById(UUID id, String currentPassword, String newPassword) throws EntityNotFoundException {
        var user = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        changePassword(user, currentPassword, newPassword);
    }

    @Override
    @Transactional
    public void changePasswordByEmail(String email, String currentPassword, String newPassword) throws EntityNotFoundException {
        var user = userRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
        changePassword(user, currentPassword, newPassword);
    }

    protected UserResult update(User user, UserUpdatePayload payload) {
        if (payload.getLocked().isPresent()) {
            boolean isAdministrator = user.getRoles().stream().anyMatch(role -> role.getName().equals(RoleName.ROLE_ADMIN));

            if (isAdministrator && !userRepository.existsAnyWithRoleExceptEmail(RoleName.ROLE_ADMIN, user.getEmail())) {
                throw new MustBeAdministrableException();
            }

            user.setLocked(payload.getLocked().get());
        }

        var updatedUser = userRepository.save(user);
        return userResultMapper.mapToResult(updatedUser);
    }

    protected void changePassword(User user, String currentPassword, String newPassword) {
        if(user.getIdentityProvider() != null) {
            throw new ExternalUserChangeNotAllowedException();
        }

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new PasswordChangeRejectedException();
        }

        user.setPassword(passwordEncoder.encode(newPassword));
    }

    @Override
    @Transactional
    public void assignRoleById(UUID id, UUID role) throws EntityNotFoundException {
        var user = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        assignsRole(user, role);
    }

    @Override
    @Transactional
    public void assignRoleByEmail(String email, UUID role) throws EntityNotFoundException {
        var user = userRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
        assignsRole(user, role);
    }

    @Transactional
    protected void assignsRole(User user, UUID roleId) throws EntityNotFoundException {
        Role role = roleRepository.findById(roleId).orElseThrow(EntityNotFoundException::new);

        user.getRoles().add(role);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void removeRoleById(UUID userId, UUID roleId) throws EntityNotFoundException {
        var user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
        removeRole(user, roleId);
    }

    @Override
    @Transactional
    public void removeRoleByEmail(String email, UUID roleId) throws EntityNotFoundException {
        var user = userRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
        removeRole(user, roleId);
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
        var user = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        delete(user);
    }

    @Override
    @Transactional
    public void deleteByEmail(String email) throws EntityNotFoundException {
        var user = userRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
        delete(user);
    }

    protected void delete(User user) {
        boolean isAdministrator = user.getRoles().stream().anyMatch(role -> role.getName().equals(RoleName.ROLE_ADMIN));

        if (isAdministrator && !userRepository.existsAnyWithRoleExceptEmail(RoleName.ROLE_ADMIN, user.getEmail())) {
            throw new MustBeAdministrableException();
        }

        if(hasActiveSubscription(user)) {
            throw new UserDeletionBlockedByActiveSubscriptionException();
        }

        userRepository.deleteSoft(user);
    }

    protected boolean hasActiveSubscription(User user) {
        var billing = user.getBilling();

        if (billing == null) return false;

        if (billing.getSubscriptionId() == null) return false;

        SubscriptionStatus status = billing.getStatus();

        return status == SubscriptionStatus.ACTIVE
                || status == SubscriptionStatus.PAST_DUE;
    }
}
