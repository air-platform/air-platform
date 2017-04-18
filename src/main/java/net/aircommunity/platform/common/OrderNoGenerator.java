package net.aircommunity.platform.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import net.aircommunity.platform.common.base.DateFormats;
import net.aircommunity.platform.common.base.SnowFlake;

/**
 * Order NO. generator.
 */
public class OrderNoGenerator {
	private static final SimpleDateFormat DATE_FORMAT = DateFormats.simple("yyMMdd");
	private final SnowFlake snowflake;

	public OrderNoGenerator(long datacenterId, long nodeId) {
		snowflake = new SnowFlake(datacenterId, nodeId);
	}

	/**
	 * Generate next order NO.
	 */
	public String next() {
		return new StringBuilder().append(DATE_FORMAT.format(new Date())).append(Long.toString(snowflake.nextId(), 32))
				.toString().toUpperCase(Locale.ENGLISH);
	}
}
