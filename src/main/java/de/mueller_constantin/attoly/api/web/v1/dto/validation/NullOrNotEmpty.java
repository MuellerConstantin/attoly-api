package de.mueller_constantin.attoly.api.web.v1.dto.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE_USE, METHOD, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = NullOrNotEmptyValidatorForString.class)
@Documented
public @interface NullOrNotEmpty {

    String message() default "{validation.dto.v1.web.de.mueller_constantin.attoly.api.NullOrNotEmpty.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
