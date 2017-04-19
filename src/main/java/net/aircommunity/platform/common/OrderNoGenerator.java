package net.aircommunity.platform.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.micro.common.DateFormats;
import io.micro.common.SnowFlake;

/**
 * Order NO. generator.
 * 
 * @author Bin.Zhang
 */
public class OrderNoGenerator {
	private static final SimpleDateFormat SAFE_FORMATTER = DateFormats.simple("yyMMdd");
	private final SnowFlake snowflake;

	public OrderNoGenerator(long datacenterId, long nodeId) {
		snowflake = new SnowFlake(datacenterId, nodeId);
	}

	/**
	 * Generate next order NO.
	 * 
	 * @return generated order number
	 */
	public String next() {
		return new StringBuilder().append(SAFE_FORMATTER.format(new Date()))
				.append(Long.toString(snowflake.nextId(), 32)).toString().toUpperCase(Locale.ENGLISH);
	}
}
