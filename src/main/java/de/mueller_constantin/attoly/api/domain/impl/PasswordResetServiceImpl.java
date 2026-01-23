package de.mueller_constantin.attoly.api.domain.impl;

import de.mueller_constantin.attoly.api.domain.EmailService;
import de.mueller_constantin.attoly.api.domain.PasswordResetService;
import de.mueller_constantin.attoly.api.domain.exception.EntityNotFoundException;
import de.mueller_constantin.attoly.api.domain.exception.InvalidResetTokenException;
import de.mueller_constantin.attoly.api.domain.exception.InvalidVerificationTokenException;
import de.mueller_constantin.attoly.api.domain.model.ResetToken;
import de.mueller_constantin.attoly.api.domain.model.User;
import de.mueller_constantin.attoly.api.repository.ResetTokenRepository;
import de.mueller_constantin.attoly.api.repository.UserRepository;
import freemarker.template.TemplateException;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.mail.MessagingException;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    private final ResetTokenRepository resetTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final String resetPasswordWebUri;
    private final String mailSender;
    private final MessageSource messageSource;

    @Autowired
    public PasswordResetServiceImpl(ResetTokenRepository resetTokenRepository,
                                    UserRepository userRepository,
                                    PasswordEncoder passwordEncoder,
                                    EmailService emailService,
                                    @Value("${attoly.web.reset-password-uri}") String resetPasswordWebUri,
                                    @Value("${attoly.mail.sender}") String mailSender,
                                    MessageSource messageSource) {
        this.resetTokenRepository = resetTokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.resetPasswordWebUri = resetPasswordWebUri;
        this.mailSender = mailSender;
        this.messageSource = messageSource;
    }

    @Override
    @Transactional
    public void sendResetMessageById(UUID id, Locale locale) throws EntityNotFoundException {
        sendResetMessage(userRepository.findById(id).orElseThrow(EntityNotFoundException::new), locale);
    }

    @Override
    @Transactional
    public void sendResetMessageByEmail(String email, Locale locale) throws EntityNotFoundException {
        sendResetMessage(userRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new), locale);
    }

    @SneakyThrows({IOException.class, MessagingException.class, TemplateException.class})
    protected void sendResetMessage(User user, Locale locale) {
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
                this.mailSender,
                this.messageSource.getMessage("ResetMail.subject", null, locale),
                "password-reset.ftl",
                Map.of("resetToken", newResetToken, "resetWebUri", resetPasswordWebUri),
                locale);
    }

    @Override
    @Transactional
    public void resetByToken(String token, String password) throws InvalidResetTokenException {
        ResetToken resetToken = resetTokenRepository.findById(token)
                .orElseThrow(InvalidVerificationTokenException::new);

        User user = userRepository.findByEmail(resetToken.getPrincipal())
                .orElseThrow(InvalidVerificationTokenException::new);

        resetTokenRepository.delete(resetToken);

        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }
}
