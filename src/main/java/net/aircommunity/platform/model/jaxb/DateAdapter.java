package net.aircommunity.platform.model.jaxb;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import net.aircommunity.platform.common.base.DateFormats;

/**
 * Adapt a string to date format
 * 
 * @author Bin Zhang
 */
public class DateAdapter extends XmlAdapter<Date, String> {
	private static final SimpleDateFormat FORMATTER = DateFormats.simple("yyyy-MM-dd");

	@Override
	public Date marshal(String date) throws Exception {
		return FORMATTER.parse(date);
	}

	@Override
	public String unmarshal(Date date) throws Exception {
		return FORMATTER.format(date);
	}

}
