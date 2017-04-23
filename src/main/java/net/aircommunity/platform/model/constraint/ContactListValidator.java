package net.aircommunity.platform.model.constraint;

import java.util.Map;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.google.common.base.Splitter;

import io.micro.annotation.constraint.Validations;
import net.aircommunity.platform.Constants;

/**
 * ContactList validator
 * 
 * @author Bin.Zhang
 */
public class ContactListValidator implements ConstraintValidator<ContactList, CharSequence> {

	@Override
	public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
		// XXX NOTE: will throw IllegalArgumentException when splitting if the format is not valid
		try {
			// <person, email>
			Map<String, String> contacts = Splitter.on(Constants.CONTACT_SEPARATOR).trimResults().omitEmptyStrings()
					.withKeyValueSeparator(Constants.CONTACT_INFO_SEPARATOR).split(value);
			return contacts.entrySet().stream().allMatch(e -> Validations.isValidEmail(e.getValue()));
		}
		catch (Exception e) {
		}
		return false;
	}

	@Override
	public void initialize(ContactList parameters) {
		// do nothing (as long as ContactList has no properties)
	}
}
