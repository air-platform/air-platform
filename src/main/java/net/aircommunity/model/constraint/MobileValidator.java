package net.aircommunity.model.constraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Mobile validator
 * 
 * @author Bin.Zhang
 */
public class MobileValidator implements ConstraintValidator<Mobile, CharSequence> {

	@Override
	public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
		return Validations.isValidChineseMobile(value);
	}

	@Override
	public void initialize(Mobile parameters) {
		// do nothing (as long as Mobile has no properties)
	}
}
