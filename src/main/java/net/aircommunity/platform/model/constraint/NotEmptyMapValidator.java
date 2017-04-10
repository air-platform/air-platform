package net.aircommunity.platform.model.constraint;

import java.util.Map;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * NotEmpty validator
 * 
 * @author Bin.Zhang
 */
public class NotEmptyMapValidator implements ConstraintValidator<NotEmpty, Map<?, ?>> {

	@Override
	public boolean isValid(Map<?, ?> value, ConstraintValidatorContext context) {
		return value == null || !value.isEmpty();
	}

	@Override
	public void initialize(NotEmpty constraintAnnotation) {
		// do nothing
	}
}