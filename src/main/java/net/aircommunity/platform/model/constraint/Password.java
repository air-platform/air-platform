package net.aircommunity.platform.model.constraint;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Pattern;

/**
 * Handy Password constraints for bean validation.
 * 
 * @author Bin.Zhang
 */
@Pattern(regexp = "^[\\w@.$%#&*\\-]{8,20}$", message = "{air.constraints.Password.pattern}")
@Constraint(validatedBy = {})
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
public @interface Password {

	String message() default "{air.constraints.Password.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
