package de.mueller_constantin.attoly.api.domain.exception;

public class PasswordChangeRejectedException extends RuntimeException{
    public PasswordChangeRejectedException() {
    }

    public PasswordChangeRejectedException(String message) {
        super(message);
    }

    public PasswordChangeRejectedException(String message, Throwable cause) {
        super(message, cause);
    }

    public PasswordChangeRejectedException(Throwable cause) {
        super(cause);
    }

    public PasswordChangeRejectedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
