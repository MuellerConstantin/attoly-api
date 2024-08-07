package de.x1c1b.attoly.api.web.v1.dto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NullOrNotEmptyValidatorForString implements ConstraintValidator<NullOrNotEmpty, String> {
    @Override
    public boolean isValid(String string, ConstraintValidatorContext constraintValidatorContext) {
        return null == string || !string.isEmpty();
    }
}
