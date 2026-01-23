package de.mueller_constantin.attoly.api.domain.exception;

public class EmailNotAllowedException extends RuntimeException {
    private final String email;

    public EmailNotAllowedException(String email) {
        this.email = email;
    }

    public EmailNotAllowedException(String message, String email) {
        super(message);
        this.email = email;
    }

    public EmailNotAllowedException(String message, Throwable cause, String email) {
        super(message, cause);
        this.email = email;
    }

    public EmailNotAllowedException(Throwable cause, String email) {
        super(cause);
        this.email = email;
    }
}
