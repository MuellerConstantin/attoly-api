package de.mueller_constantin.attoly.api.domain.exception;

public class ExpirableShortcutLimitExceededException extends RuntimeException {
    public ExpirableShortcutLimitExceededException() {
    }

    public ExpirableShortcutLimitExceededException(String message) {
        super(message);
    }

    public ExpirableShortcutLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExpirableShortcutLimitExceededException(Throwable cause) {
        super(cause);
    }

    public ExpirableShortcutLimitExceededException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
