package net.aircommunity.model.constraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Handy username constraints for bean validation.
 * 
 * @author Bin.Zhang
 */
public class UsernameValidator implements ConstraintValidator<Username, CharSequence> {

	@Override
	public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
		return Validations.isValidUsername(value);
	}

	@Override
	public void initialize(Username parameters) {
		// do nothing (as long as Email has no properties)
	}
}
