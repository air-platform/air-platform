package net.aircommunity.platform.model.constraint;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micro.common.Strings;

/**
 * TimeSlot validator (pattern: HH:mm-HH:mm, e.g. 08:00-09:00, also accept H:m, e.g. 8:00-9:00, and will re-formated to
 * HH:mm)
 * 
 * @author Bin.Zhang
 */
public class TimeSlotValidator implements ConstraintValidator<TimeSlot, CharSequence> {
	private static final Logger LOG = LoggerFactory.getLogger(TimeSlotValidator.class);

	private static final String TIMESLOT_SPARATOR = "-";
	private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("H:m", Locale.ENGLISH);

	@Override
	public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
		// allow null or empty
		if (value == null || Strings.isBlank(value.toString())) {
			return true;
		}
		String[] parts = value.toString().split(TIMESLOT_SPARATOR);
		if (parts.length != 2) {
			return false;
		}
		try {
			LocalTime timeFrom = LocalTime.parse(parts[0].trim(), DATETIME_FORMATTER);
			LocalTime timeTo = LocalTime.parse(parts[1].trim(), DATETIME_FORMATTER);
			return timeFrom.isBefore(timeTo);
		}
		catch (Exception e) {
			LOG.error(String.format("Failed to parse timeslot: %s, cause: %s", value, e.getMessage()), e);
		}
		return false;
	}

	@Override
	public void initialize(TimeSlot parameters) {
		// do nothing (as long as TimeSlot has no properties)
	}
}
