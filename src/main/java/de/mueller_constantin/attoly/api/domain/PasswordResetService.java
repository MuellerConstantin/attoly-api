package de.mueller_constantin.attoly.api.domain;

import de.mueller_constantin.attoly.api.domain.exception.EntityNotFoundException;
import de.mueller_constantin.attoly.api.domain.exception.InvalidResetTokenException;

import java.util.UUID;

/**
 * Contains operations to reset an account password extraordinarily.
 */
public interface PasswordResetService {

    /**
     * Generates and sends a reset token to reset a password.
     *
     * @param id The identifier of the user for which a token should be sent.
     * @throws EntityNotFoundException Thrown if the user cannot be found.
     */
    void sendResetMessageById(UUID id) throws EntityNotFoundException;

    /**
     * Generates and sends a reset token to reset a password.
     *
     * @param email The email of the user for which a token should be sent.
     * @throws EntityNotFoundException Thrown if the user cannot be found.
     */
    void sendResetMessageByEmail(String email) throws EntityNotFoundException;

    /**
     * Resets a user's password using a token. This step is necessary, for example, if a user can no
     * longer remember a password.
     *
     * @param token    The reset token to use.
     * @param password The new password to use.
     * @throws InvalidResetTokenException Thrown if the reset token doesn't longer exist.
     */
    void resetByToken(String token, String password) throws InvalidResetTokenException;
}
