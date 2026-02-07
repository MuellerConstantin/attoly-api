package de.mueller_constantin.attoly.api.domain.exception;

public class FeatureNotAvailableException extends RuntimeException {
    public FeatureNotAvailableException() {
    }

    public FeatureNotAvailableException(String message) {
        super(message);
    }

    public FeatureNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public FeatureNotAvailableException(Throwable cause) {
        super(cause);
    }

    public FeatureNotAvailableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
