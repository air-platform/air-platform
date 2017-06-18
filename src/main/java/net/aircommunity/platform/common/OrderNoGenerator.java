package net.aircommunity.platform.common;

import java.util.Locale;

import io.micro.common.SnowFlake;

/**
 * Order NO. generator.
 * 
 * @author Bin.Zhang
 */
public final class OrderNoGenerator {
	// private static final SimpleDateFormat SAFE_FORMATTER = DateFormats.simple("yyMMdd");
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
		// return new StringBuilder().append(SAFE_FORMATTER.format(new Date()))
		// .append(Long.toString(snowflake.nextId(), 32)).toString().toUpperCase(Locale.ENGLISH);
		return Long.toString(snowflake.nextId(), 16).toString().toUpperCase(Locale.ENGLISH);
	}
}
