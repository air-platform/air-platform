package net.aircommunity.platform.model.constraint;

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
 * Description: annotation to validate an TimeSlot (by pattern)
 * 
 * @author Bin.Zhang
 */
@Documented
@Constraint(validatedBy = TimeSlotValidator.class)
@Target({ METHOD, FIELD, ANNOTATION_TYPE, PARAMETER })
@Retention(RUNTIME)
public @interface TimeSlot {

	String message() default "{air.constraints.TimeSlot.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
