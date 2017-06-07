package net.aircommunity.platform.model.jaxb;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import io.micro.common.DateFormats;

/**
 * Adapt a string to date format
 * 
 * @author Bin Zhang
 */
public class DateAdapter extends XmlAdapter<String, Date> {
	private static final SimpleDateFormat SAFE_FORMATTER = DateFormats.simple("yyyy-MM-dd");

	@Override
	public String marshal(Date date) throws Exception {
		return SAFE_FORMATTER.format(date);
	}

	@Override
	public Date unmarshal(String date) throws Exception {
		return SAFE_FORMATTER.parse(date);
	}

}
