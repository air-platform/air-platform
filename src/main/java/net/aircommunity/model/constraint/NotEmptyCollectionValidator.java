package net.aircommunity.model.constraint;

import java.util.Collection;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * NotEmpty validator
 * 
 * @author Bin.Zhang
 */
public class NotEmptyCollectionValidator implements ConstraintValidator<NotEmpty, Collection<?>> {

	@Override
	public boolean isValid(Collection<?> value, ConstraintValidatorContext context) {
		return value == null || !value.isEmpty();
	}

	@Override
	public void initialize(NotEmpty constraintAnnotation) {
		// do nothing
	}
}
