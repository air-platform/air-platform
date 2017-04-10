package net.aircommunity.platform.model.constraint;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Handy username constraints for bean validation.
 * 
 * @author Bin.Zhang
 */
@Constraint(validatedBy = UsernameValidator.class)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ METHOD, PARAMETER, FIELD, ANNOTATION_TYPE })
public @interface Username {

	String message() default "{air.constraints.Username.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
