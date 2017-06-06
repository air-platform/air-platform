package net.aircommunity.platform.model.constraint;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.google.common.base.Splitter;

import io.micro.common.Strings;
import net.aircommunity.platform.Constants;

/**
 * PriceList validator
 * 
 * @author Bin.Zhang
 */
public class PriceListValidator implements ConstraintValidator<PriceList, CharSequence> {

	@Override
	public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
		// allow null or empty
		if (value == null || Strings.isBlank(value.toString())) {
			return true;
		}
		// XXX NOTE: will throw IllegalArgumentException when splitting if the format is not valid
		try {
			// <person, email>
			List<String> list = Splitter.on(Constants.PRICE_SEPARATOR).trimResults().omitEmptyStrings()
					.splitToList(value);
			return list.stream().allMatch(this::isValid);
		}
		catch (Exception e) {
		}
		return false;
	}

	private boolean isValid(String price) {
		try {
			Double.parseDouble(price);
			return true;
		}
		catch (NumberFormatException e) {
		}
		return false;
	}

	@Override
	public void initialize(PriceList parameters) {
		// do nothing (as long as ContactList has no properties)
	}

}
