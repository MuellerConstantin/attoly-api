package de.mueller_constantin.attoly.api.domain.exception;

public class NoCustomerYetException extends RuntimeException {
    public NoCustomerYetException() {
    }

    public NoCustomerYetException(String message) {
        super(message);
    }

    public NoCustomerYetException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoCustomerYetException(Throwable cause) {
        super(cause);
    }

    public NoCustomerYetException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
