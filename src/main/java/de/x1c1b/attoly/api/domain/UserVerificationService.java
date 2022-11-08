package de.x1c1b.attoly.api.domain;

import de.x1c1b.attoly.api.domain.exception.EntityNotFoundException;
import de.x1c1b.attoly.api.domain.exception.InvalidVerificationTokenException;

import java.util.UUID;

/**
 * Service is used to verify e-mails and user accounts.
 */
public interface UserVerificationService {

    /**
     * Generates and sends a verification token. Verification tokens are used to verify a user's account and email.
     *
     * @param id The identifier of the user for which a token should be sent.
     * @throws EntityNotFoundException Thrown if the user cannot be found.
     */
    void sendVerificationMessageById(UUID id) throws EntityNotFoundException;

    /**
     * Generates and sends a verification token. Verification tokens are used to verify a user's account and email.
     *
     * @param email The email of the user for which a token should be sent.
     * @throws EntityNotFoundException Thrown if the user cannot be found.
     */
    void sendVerificationMessageByEmail(String email) throws EntityNotFoundException;

    /**
     * Verifies and activates a user account using a verification token that was previously generated and sent.
     *
     * @param token The verification token to use.
     * @throws InvalidVerificationTokenException Thrown if the verification token doesn't longer exist.
     */
    void verifyByToken(String token) throws InvalidVerificationTokenException;
}
