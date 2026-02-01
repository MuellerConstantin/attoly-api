package de.mueller_constantin.attoly.api.domain.exception;

public class ExternalUserChangeNotAllowedException extends RuntimeException {
    public ExternalUserChangeNotAllowedException() {
    }

    public ExternalUserChangeNotAllowedException(String message) {
        super(message);
    }

    public ExternalUserChangeNotAllowedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExternalUserChangeNotAllowedException(Throwable cause) {
        super(cause);
    }

    public ExternalUserChangeNotAllowedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
