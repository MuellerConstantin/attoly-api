package de.x1c1b.attoly.api.domain.impl;

import de.x1c1b.attoly.api.domain.*;
import de.x1c1b.attoly.api.domain.model.ResetToken;
import de.x1c1b.attoly.api.domain.model.Role;
import de.x1c1b.attoly.api.domain.model.User;
import de.x1c1b.attoly.api.domain.model.VerificationToken;
import de.x1c1b.attoly.api.domain.payload.UserCreationPayload;
import de.x1c1b.attoly.api.domain.payload.UserUpdatePayload;
import de.x1c1b.attoly.api.repository.ResetTokenRepository;
import de.x1c1b.attoly.api.repository.RoleRepository;
import de.x1c1b.attoly.api.repository.UserRepository;
import de.x1c1b.attoly.api.repository.VerificationTokenRepository;
import freemarker.template.TemplateException;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ResetTokenRepository resetTokenRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private final String verifyUserWebUri;

    private final String resetPasswordWebUri;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           ResetTokenRepository resetTokenRepository,
                           VerificationTokenRepository verificationTokenRepository,
                           PasswordEncoder passwordEncoder,
                           EmailService emailService,
                           @Value("${attoly.web.verify-user-uri}") String verifyUserWebUri,
                           @Value("${attoly.web.reset-password-uri}") String resetPasswordWebUri) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.resetTokenRepository = resetTokenRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.verifyUserWebUri = verifyUserWebUri;
        this.resetPasswordWebUri = resetPasswordWebUri;
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

        User newUser = userRepository.save(user);
        sendVerificationMessage(newUser);

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

    @Override
    public void sendVerificationMessageById(UUID id) {
        sendVerificationMessage(findById(id));
    }

    @Override
    public void sendVerificationMessageByEmail(String email) {
        sendVerificationMessage(findByEmail(email));
    }

    @SneakyThrows({IOException.class, MessagingException.class, TemplateException.class})
    protected void sendVerificationMessage(User user) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] secret = new byte[6];

        secureRandom.nextBytes(secret);
        String token = Base64.getEncoder().encodeToString(secret);

        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .principal(user.getEmail())
                .build();

        VerificationToken newVerificationToken = verificationTokenRepository.save(verificationToken);

        emailService.sendTemplateMessage(user.getEmail(),
                "noreply@attoly.com",
                "Attoly Account Verification",
                "user-verification.ftlh",
                Map.of("verificationToken", newVerificationToken, "verificationWebUri", verifyUserWebUri));
    }

    @Override
    @Transactional
    public void verifyByToken(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findById(token)
                .orElseThrow(InvalidVerificationTokenException::new);

        User user = userRepository.findByEmail(verificationToken.getPrincipal())
                .orElseThrow(InvalidVerificationTokenException::new);

        user.setEmailVerified(true);
        userRepository.save(user);
    }

    @Override
    public void sendResetMessageById(UUID id) {
        sendResetMessage(findById(id));
    }

    @Override
    public void sendResetMessageByEmail(String email) {
        sendResetMessage(findByEmail(email));
    }

    @SneakyThrows({IOException.class, MessagingException.class, TemplateException.class})
    protected void sendResetMessage(User user) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] secret = new byte[6];

        secureRandom.nextBytes(secret);
        String token = Base64.getEncoder().encodeToString(secret);

        ResetToken resetToken = ResetToken.builder()
                .token(token)
                .principal(user.getEmail())
                .build();

        ResetToken newResetToken = resetTokenRepository.save(resetToken);

        emailService.sendTemplateMessage(user.getEmail(),
                "noreply@attoly.com",
                "Attoly Password Reset",
                "password-reset.ftlh",
                Map.of("resetToken", newResetToken, "resetWebUri", resetPasswordWebUri));
    }

    @Override
    public void resetPasswordByToken(String token, String newPassword) {
        ResetToken resetToken = resetTokenRepository.findById(token)
                .orElseThrow(InvalidVerificationTokenException::new);

        User user = userRepository.findByEmail(resetToken.getPrincipal())
                .orElseThrow(InvalidVerificationTokenException::new);

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
