package net.aircommunity.platform.common.base;

import java.lang.ref.SoftReference;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.concurrent.ThreadSafe;

/**
 * DateFormat utility.
 * 
 * @author Bin.Zhang
 */
public final class DateFormats {

	/**
	 * Create a thread safe {@code SimpleDateFormat}.
	 * 
	 * @param pattern the pattern describing the date and time format
	 * @return SimpleDateFormat
	 */
	public static SimpleDateFormat simple(String pattern) {
		return new SafeSimpleDateFormat(pattern);
	}

	/**
	 * The main idea of this class is storing separate instances of SimpleDateFormat for separate Threads in ThreadLocal
	 * variable. If 2 concurrent threads try to parse date, the will use 2 different instances of SimpleDateFormat.
	 * 
	 * <p>
	 * Thread-safe version of java.text.SimpleDateFormat. You can declare it as a static final variable:
	 * {@code private static final SafeSimpleDateFormat DATE_FORMAT = new SafeSimpleDateFormat(DATE_PATTERN);}
	 */
	@ThreadSafe
	private static class SafeSimpleDateFormat extends SimpleDateFormat {
		private static final long serialVersionUID = 1L;
		private final ThreadLocal<SoftReference<SimpleDateFormat>> formatCache = new ThreadLocal<SoftReference<SimpleDateFormat>>() {
			@Override
			public SoftReference<SimpleDateFormat> get() {
				SoftReference<SimpleDateFormat> softRef = super.get();
				if (softRef == null || softRef.get() == null) {
					softRef = new SoftReference<SimpleDateFormat>(new SimpleDateFormat(dateFormat));
					super.set(softRef);
				}
				return softRef;
			}
		};

		private final String dateFormat;

		/**
		 * Construct with data format.
		 * 
		 * @param dateFormat the data format
		 */
		public SafeSimpleDateFormat(String dateFormat) {
			this.dateFormat = dateFormat;
		}

		private DateFormat getDateFormat() {
			return formatCache.get().get();
		}

		@Override
		public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
			return getDateFormat().format(date, toAppendTo, fieldPosition);
		}

		@Override
		public Date parse(String source, ParsePosition pos) {
			return getDateFormat().parse(source, pos);
		}
	}

	private DateFormats() {
		throw new AssertionError();
	}
}
