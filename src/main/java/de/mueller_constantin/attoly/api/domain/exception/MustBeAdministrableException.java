package de.mueller_constantin.attoly.api.domain.exception;

/**
 * Thrown when an operation would remove the last administrator from the system.
 */
public class MustBeAdministrableException extends RuntimeException {

    public MustBeAdministrableException() {
    }

    public MustBeAdministrableException(String message) {
        super(message);
    }

    public MustBeAdministrableException(String message, Throwable cause) {
        super(message, cause);
    }

    public MustBeAdministrableException(Throwable cause) {
        super(cause);
    }
}
