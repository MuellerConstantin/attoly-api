package de.x1c1b.attoly.api.domain.exception;

/**
 * Thrown when an invalid reset token is used to reset the password.
 */
public class InvalidResetTokenException extends RuntimeException {

    public InvalidResetTokenException() {
    }

    public InvalidResetTokenException(String message) {
        super(message);
    }

    public InvalidResetTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidResetTokenException(Throwable cause) {
        super(cause);
    }
}
