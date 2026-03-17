package de.mueller_constantin.attoly.api.domain.exception;

public class ShortcutPasswordRequiredException extends RuntimeException {
    public ShortcutPasswordRequiredException() {
    }

    public ShortcutPasswordRequiredException(String message) {
        super(message);
    }

    public ShortcutPasswordRequiredException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShortcutPasswordRequiredException(Throwable cause) {
        super(cause);
    }

    public ShortcutPasswordRequiredException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
