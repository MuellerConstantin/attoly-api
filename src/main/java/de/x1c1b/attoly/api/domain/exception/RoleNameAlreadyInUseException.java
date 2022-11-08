package de.x1c1b.attoly.api.domain.exception;

import lombok.Getter;

@Getter
public class RoleNameAlreadyInUseException extends RuntimeException {

    private final String name;

    public RoleNameAlreadyInUseException(String name) {
        this.name = name;
    }

    public RoleNameAlreadyInUseException(String message, String name) {
        super(message);
        this.name = name;
    }

    public RoleNameAlreadyInUseException(String message, Throwable cause, String name) {
        super(message, cause);
        this.name = name;
    }

    public RoleNameAlreadyInUseException(Throwable cause, String name) {
        super(cause);
        this.name = name;
    }
}
