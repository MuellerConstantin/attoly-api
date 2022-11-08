package de.x1c1b.attoly.api.domain.exception;

/**
 * Thrown when an entity cannot be found. The entity no longer exists or never existed.
 */
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException() {
    }

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityNotFoundException(Throwable cause) {
        super(cause);
    }
}
