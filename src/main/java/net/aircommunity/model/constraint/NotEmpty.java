package net.aircommunity.model.constraint;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * NotEmpty Constraint
 * 
 * @author Bin.Zhang
 */
@Documented
@Constraint(validatedBy = { NotEmptyCollectionValidator.class, NotEmptyMapValidator.class, NotEmptyValidator.class })
@Target({ METHOD, FIELD, ANNOTATION_TYPE, PARAMETER })
@Retention(RUNTIME)
public @interface NotEmpty {
	Class<?>[] groups() default {};

	String message() default "{air.constraints.NotEmpty.message}";

	Class<? extends Payload>[] payload() default {};
}
