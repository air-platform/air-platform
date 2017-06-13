package net.aircommunity.platform.common;

import java.math.BigDecimal;

/**
 * Order Price Helper
 * 
 * @author Bin.Zhang
 */
public final class OrderPrices {

	/**
	 * Normalize price.
	 * 
	 * @param price the price
	 * @return BigDecimal
	 */
	public static BigDecimal normalizePrice(BigDecimal price) {
		// FIXME ROUND_HALF_DOWN? if ROUND_HALF_UP -> customer will pay more than actual
		return price.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * Check price matches.
	 * 
	 * @param actual the actual price
	 * @param expect the actual expect
	 * @return true if matches, false otherwise
	 */
	public static boolean priceMatches(BigDecimal actual, BigDecimal expect) {
		return actual.compareTo(expect) == 0;
	}

}
