package de.mueller_constantin.attoly.api.repository.rsql;

import lombok.Getter;

@Getter
public class UnsupportedFilterQueryFieldException extends RuntimeException {
    private final String fieldName;

    public UnsupportedFilterQueryFieldException(String fieldName) {
        this.fieldName = fieldName;
    }

    public UnsupportedFilterQueryFieldException(String fieldName, String message) {
        super(message);
        this.fieldName = fieldName;
    }

    public UnsupportedFilterQueryFieldException(String fieldName, String message, Throwable cause) {
        super(message, cause);
        this.fieldName = fieldName;
    }

    public UnsupportedFilterQueryFieldException(String fieldName, Throwable cause) {
        super(cause);
        this.fieldName = fieldName;
    }

    public UnsupportedFilterQueryFieldException(String fieldName, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.fieldName = fieldName;
    }
}
