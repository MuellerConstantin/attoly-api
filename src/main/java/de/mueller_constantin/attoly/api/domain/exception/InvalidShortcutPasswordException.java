package de.mueller_constantin.attoly.api.domain.exception;

public class InvalidShortcutPasswordException extends RuntimeException {
    public InvalidShortcutPasswordException() {
    }

    public InvalidShortcutPasswordException(String message) {
        super(message);
    }

    public InvalidShortcutPasswordException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidShortcutPasswordException(Throwable cause) {
        super(cause);
    }

    public InvalidShortcutPasswordException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
