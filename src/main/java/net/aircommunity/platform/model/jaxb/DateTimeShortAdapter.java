package net.aircommunity.platform.model.jaxb;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import io.micro.common.DateFormats;

/**
 * Adapt a string to date time format
 * 
 * @author Bin Zhang
 */
public class DateTimeShortAdapter extends XmlAdapter<Date, String> {
	private static final SimpleDateFormat SAFE_FORMATTER = DateFormats.simple("yyyy-MM-dd HH:mm");

	@Override
	public Date marshal(String date) throws Exception {
		return SAFE_FORMATTER.parse(date);
	}

	@Override
	public String unmarshal(Date date) throws Exception {
		return SAFE_FORMATTER.format(date);
	}

}
