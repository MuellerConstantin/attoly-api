package de.mueller_constantin.attoly.api.web.v1.rsql;

import lombok.Getter;

@Getter
public class UnsupportedFilterQueryOperatorException extends RuntimeException {
    private final String fieldName;
    private final String operator;

    public UnsupportedFilterQueryOperatorException(String fieldName, String operator) {
        this.fieldName = fieldName;
        this.operator = operator;
    }

    public UnsupportedFilterQueryOperatorException(String fieldName, String operator, String message) {
        super(message);
        this.fieldName = fieldName;
        this.operator = operator;
    }

    public UnsupportedFilterQueryOperatorException(String fieldName, String operator, String message, Throwable cause) {
        super(message, cause);
        this.fieldName = fieldName;
        this.operator = operator;
    }

    public UnsupportedFilterQueryOperatorException(String fieldName, String operator, Throwable cause) {
        super(cause);
        this.fieldName = fieldName;
        this.operator = operator;
    }

    public UnsupportedFilterQueryOperatorException(String fieldName, String operator, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.fieldName = fieldName;
        this.operator = operator;
    }
}
