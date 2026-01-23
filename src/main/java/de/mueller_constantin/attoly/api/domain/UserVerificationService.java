package de.mueller_constantin.attoly.api.domain;

import de.mueller_constantin.attoly.api.domain.exception.EntityNotFoundException;
import de.mueller_constantin.attoly.api.domain.exception.InvalidVerificationTokenException;

import java.util.Locale;
import java.util.UUID;

/**
 * Service is used to verify e-mails and user accounts.
 */
public interface UserVerificationService {

    /**
     * Generates and sends a verification token. Verification tokens are used to verify a user's account and email.
     *
     * @param id The identifier of the user for which a token should be sent.
     * @param locale The locale to use when sending the verification message.
     * @throws EntityNotFoundException Thrown if the user cannot be found.
     */
    void sendVerificationMessageById(UUID id, Locale locale) throws EntityNotFoundException;

    /**
     * Generates and sends a verification token. Verification tokens are used to verify a user's account and email.
     *
     * @param email The email of the user for which a token should be sent.
     * @param locale The locale to use when sending the verification message.
     * @throws EntityNotFoundException Thrown if the user cannot be found.
     */
    void sendVerificationMessageByEmail(String email, Locale locale) throws EntityNotFoundException;

    /**
     * Verifies and activates a user account using a verification token that was previously generated and sent.
     *
     * @param token The verification token to use.
     * @throws InvalidVerificationTokenException Thrown if the verification token doesn't longer exist.
     */
    void verifyByToken(String token) throws InvalidVerificationTokenException;
}
