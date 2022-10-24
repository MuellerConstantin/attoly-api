package de.x1c1b.attoly.api.domain;

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
