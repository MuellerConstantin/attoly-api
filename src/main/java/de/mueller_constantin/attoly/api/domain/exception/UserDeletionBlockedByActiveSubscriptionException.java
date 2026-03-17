package de.mueller_constantin.attoly.api.domain.exception;

public class UserDeletionBlockedByActiveSubscriptionException extends RuntimeException {
    public UserDeletionBlockedByActiveSubscriptionException() {
    }

    public UserDeletionBlockedByActiveSubscriptionException(String message) {
        super(message);
    }

    public UserDeletionBlockedByActiveSubscriptionException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserDeletionBlockedByActiveSubscriptionException(Throwable cause) {
        super(cause);
    }

    public UserDeletionBlockedByActiveSubscriptionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
