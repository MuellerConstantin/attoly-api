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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.mail.MessagingException;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@Service
public class UserVerificationServiceImpl implements UserVerificationService {

    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final String verifyUserWebUri;

    @Autowired
    public UserVerificationServiceImpl(VerificationTokenRepository verificationTokenRepository,
                                       UserRepository userRepository,
                                       EmailService emailService,
                                       @Value("${attoly.web.verify-user-uri}") String verifyUserWebUri) {
        this.verificationTokenRepository = verificationTokenRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.verifyUserWebUri = verifyUserWebUri;
    }

    @Override
    @Transactional
    public void sendVerificationMessageById(UUID id) throws EntityNotFoundException {
        sendVerificationMessage(userRepository.findById(id).orElseThrow(EntityNotFoundException::new));
    }

    @Override
    @Transactional
    public void sendVerificationMessageByEmail(String email) throws EntityNotFoundException {
        sendVerificationMessage(userRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new));
    }

    @SneakyThrows({IOException.class, MessagingException.class, TemplateException.class})
    protected void sendVerificationMessage(User user) {
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
                "noreply@attoly.com",
                "Attoly Account Verification",
                "user-verification.ftl",
                Map.of("verificationToken", newVerificationToken, "verificationWebUri", verifyUserWebUri));
    }

    @Override
    @Transactional
    public void verifyByToken(String token) throws InvalidVerificationTokenException {
        VerificationToken verificationToken = verificationTokenRepository.findById(token)
                .orElseThrow(InvalidVerificationTokenException::new);

        User user = userRepository.findByEmail(verificationToken.getPrincipal())
                .orElseThrow(InvalidVerificationTokenException::new);

        user.setEmailVerified(true);
        userRepository.save(user);
    }
}
