package net.aircommunity.model.constraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Email validator
 * 
 * @author Bin.Zhang
 */
public class EmailValidator implements ConstraintValidator<Email, CharSequence> {

	@Override
	public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
		return Validations.isValidEmail(value);
	}

	@Override
	public void initialize(Email parameters) {
		// do nothing (as long as Email has no properties)
	}
}
