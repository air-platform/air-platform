package net.aircommunity.platform.common;

import java.math.BigDecimal;

/**
 * Order Price Helper
 * 
 * @author Bin.Zhang
 */
public final class OrderPrices {

	public static BigDecimal normalizePrice(BigDecimal price) {
		// FIXME ROUND_HALF_DOWN? if ROUND_HALF_UP -> customer will pay more than actual
		return price.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	public static boolean priceMatches(BigDecimal actual, BigDecimal expect) {
		return actual.compareTo(expect) == 0;
	}

	public static void main(String argp[]) {
		System.out.println(normalizePrice(BigDecimal.valueOf(1.3353433)).toString());
	}

}
