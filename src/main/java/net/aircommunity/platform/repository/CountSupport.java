package net.aircommunity.platform.repository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.function.BiFunction;

import io.micro.common.DateTimes;
import io.micro.common.function.TriFunction;
import net.aircommunity.platform.Configuration;

/**
 * Generic count support.
 * 
 * @author Bin.Zhang
 */
final class CountSupport {

	/**
	 * Count new records for current month. (e.g. 2017-07-01 00:00:00 - 2017-07-01 23:59:59)
	 * 
	 * @param tenantId the tenantId
	 * @param counter the counter
	 * @return count of new records
	 */
	public static long countThisMonth(String tenantId, TriFunction<String, Date, Date, Long> counter) {
		ZoneId zone = Configuration.getZoneId();
		LocalDateTime now = LocalDateTime.now();
		Date firstDayOfMonth = DateTimes.getFirstDayOfMonth(now, zone);
		Date lastDayOfMonth = DateTimes.getLastDayOfMonth(now, zone);
		return counter.apply(tenantId, firstDayOfMonth, lastDayOfMonth);
	}

	/**
	 * Count new records for current month. (e.g. 2017-07-01 00:00:00 - 2017-07-01 23:59:59)
	 * 
	 * @param counter the counter
	 * @return count of new records
	 */
	public static long countThisMonth(BiFunction<Date, Date, Long> counter) {
		ZoneId zone = Configuration.getZoneId();
		LocalDateTime now = LocalDateTime.now();
		Date firstDayOfMonth = DateTimes.getFirstDayOfMonth(now, zone);
		Date lastDayOfMonth = DateTimes.getLastDayOfMonth(now, zone);
		return counter.apply(firstDayOfMonth, lastDayOfMonth);
	}

	/**
	 * Count new records for today. (e.g. 2017-07-11 00:00:00 - 2017-07-11 23:59:59)
	 * 
	 * @param tenantId the tenantId
	 * @param counter the counter
	 * @return count of new records
	 */
	public static long countToday(String tenantId, TriFunction<String, Date, Date, Long> counter) {
		ZoneId zone = Configuration.getZoneId();
		LocalDateTime now = LocalDateTime.now();
		Date from = DateTimes.getStartOfDay(now, zone);
		Date to = DateTimes.getEndOfDay(now, zone);
		return counter.apply(tenantId, from, to);
	}

	/**
	 * Count new records for today. (e.g. 2017-07-11 00:00:00 - 2017-07-11 23:59:59)
	 * 
	 * @param counter the counter
	 * @return count of new records
	 */
	public static long countToday(BiFunction<Date, Date, Long> counter) {
		ZoneId zone = Configuration.getZoneId();
		LocalDateTime now = LocalDateTime.now();
		Date from = DateTimes.getStartOfDay(now, zone);
		Date to = DateTimes.getEndOfDay(now, zone);
		return counter.apply(from, to);
	}

}
