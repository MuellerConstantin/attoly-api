package de.mueller_constantin.attoly.api.domain.impl;

import de.mueller_constantin.attoly.api.domain.EmailService;
import de.mueller_constantin.attoly.api.domain.UserVerificationService;
import de.mueller_constantin.attoly.api.domain.exception.EntityNotFoundException;
import de.mueller_constantin.attoly.api.domain.exception.InvalidVerificationTokenException;
import de.mueller_constantin.attoly.api.domain.model.User;
import de.mueller_constantin.attoly.api.domain.model.VerificationToken;
import de.mueller_constantin.attoly.api.repository.UserRepository;
import de.mueller_constantin.attoly.api.repository.VerificationTokenRepository;
import freemarker.template.TemplateException;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
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
public class UserVerificationServiceImpl implements UserVerificationService {

    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final String verifyUserWebUri;
    private final String mailSender;
    private final MessageSource messageSource;

    @Autowired
    public UserVerificationServiceImpl(VerificationTokenRepository verificationTokenRepository,
                                       UserRepository userRepository,
                                       EmailService emailService,
                                       @Value("${attoly.web.verify-user-uri}") String verifyUserWebUri,
                                       @Value("${attoly.mail.sender}") String mailSender,
                                       MessageSource messageSource) {
        this.verificationTokenRepository = verificationTokenRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.verifyUserWebUri = verifyUserWebUri;
        this.mailSender = mailSender;
        this.messageSource = messageSource;
    }

    @Override
    @Transactional
    public void sendVerificationMessageById(UUID id, Locale locale) throws EntityNotFoundException {
        sendVerificationMessage(userRepository.findById(id).orElseThrow(EntityNotFoundException::new), locale);
    }

    @Override
    @Transactional
    public void sendVerificationMessageByEmail(String email, Locale locale) throws EntityNotFoundException {
        sendVerificationMessage(userRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new), locale);
    }

    @SneakyThrows({IOException.class, MessagingException.class, TemplateException.class})
    protected void sendVerificationMessage(User user, Locale locale) {
        if(user.isEmailVerified()) {
            return;
        }

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
                this.mailSender,
                this.messageSource.getMessage("VerifyMail.subject", null, locale),
                "user-verification.ftl",
                Map.of("verificationToken", newVerificationToken, "verificationWebUri", verifyUserWebUri),
                locale);
    }

    @Override
    @Transactional
    public void verifyByToken(String token) throws InvalidVerificationTokenException {
        VerificationToken verificationToken = verificationTokenRepository.findById(token)
                .orElseThrow(InvalidVerificationTokenException::new);

        User user = userRepository.findByEmail(verificationToken.getPrincipal())
                .orElseThrow(InvalidVerificationTokenException::new);

        verificationTokenRepository.delete(verificationToken);

        user.setEmailVerified(true);
        userRepository.save(user);
    }
}
