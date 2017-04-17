package net.aircommunity.platform.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import net.aircommunity.platform.common.base.DateFormats;

/**
 * 订单号生成规则:
 * 
 * <br>
 * 正常情况下:年(2位)+月(2位)+日(2位)+秒(5位)+循环自增值(3位)+随机码(2位)共16位
 * 
 * <br>
 * 非正常情况下:年(2位)+月(2位)+日(2位)+秒(5位)+随机码(5位)共16位
 * 
 * <br>
 * 注:非正常情况在同时开启1W个线程的情况下并未发生 业务编码(1位)：自营、通用件、全车件待定
 */
public class OrderNoGenerator {
	public static final OrderNoGenerator INSTANCE = new OrderNoGenerator();

	private static final SimpleDateFormat DATE_FORMAT = DateFormats.simple("yyMMddHHmmss");
	// if overflow start from init num
	private static final int MAX_LOOP = 999;
	private static final int INIT_NUM = 100;
	private final AtomicInteger counter;

	private OrderNoGenerator() {
		counter = new AtomicInteger(INIT_NUM);
	}

	/**
	 * Generate next order NO.
	 */
	public Long nextOrderNo() {
		int nextValue = counter.getAndIncrement();
		// overflow?
		if (nextValue > MAX_LOOP) {
			// CAS update
			if (counter.compareAndSet(nextValue + 1, INIT_NUM)) {
				counter.getAndIncrement();
				return nextNormally(INIT_NUM);
			}

			// CAS failure, retry
			nextValue = counter.getAndIncrement();
			if (nextValue <= MAX_LOOP) {
				return nextNormally(nextValue);
			}
			// fallback
			return nextUnnormally();
		}
		return nextNormally(nextValue);
	}

	private Long nextNormally(int nextValue) {
		int randomNum = (int) (Math.random() * 90) + 10;// 2 digit random
		return Long.valueOf(getDateNum() + nextValue + randomNum);
	}

	private Long nextUnnormally() {
		int randomNum = (int) (Math.random() * 90000) + 10000;// 5 digit random
		return Long.valueOf(getDateNum() + randomNum);
	}

	/**
	 * generate time part of this order NO.
	 */
	private static String getDateNum() {
		String time = DATE_FORMAT.format(new Date());
		int seconds = Integer.valueOf(time.substring(6, 8)) * 60 * 60 + Integer.valueOf(time.substring(8, 10)) * 60
				+ Integer.valueOf(time.substring(10, 12));

		String secondsStr = String.valueOf(seconds);
		StringBuilder builder = new StringBuilder(time.substring(0, 6)).append(secondsStr);
		int len = secondsStr.length();
		if (len < 5) {
			for (int i = 5, j = len; i > j; i--) {
				builder.append("0").append(secondsStr);
			}
		}
		return builder.toString();
	}

	public static void main(String arg[]) {
		OrderNoGenerator generator = OrderNoGenerator.INSTANCE;
		System.out.println(generator.nextOrderNo());
		System.out.println(generator.nextOrderNo());
		System.out.println(generator.nextOrderNo());
	}

}
