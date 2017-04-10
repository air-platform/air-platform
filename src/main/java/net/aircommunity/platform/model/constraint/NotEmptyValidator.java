package net.aircommunity.platform.model.constraint;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * NotEmpty validator
 * 
 * @author Bin.Zhang
 */
public class NotEmptyValidator implements ConstraintValidator<NotEmpty, String> {

	@Override
	public void initialize(NotEmpty constraintAnnotation) {
		// do nothing
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null) {
			return false;
		}
		if (value.getClass().isArray()) {
			return Array.getLength(value) > 0;
		}
		try {
			final Method isEmptyMethod = value.getClass().getMethod("isEmpty");
			if (isEmptyMethod != null) {
				return !((Boolean) isEmptyMethod.invoke(value)).booleanValue();
			}
		}
		catch (IllegalAccessException iae) {
			// do nothing
		}
		catch (NoSuchMethodException nsme) {
			// do nothing
		}
		catch (InvocationTargetException ite) {
			// do nothing
		}
		return !value.toString().isEmpty();
	}

}
