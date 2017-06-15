package net.aircommunity.platform.common;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
	 * 金额，转换单位： 元－>分 (convert to smallest unit)
	 * 
	 * @param price the price in standard unit our platform
	 * @return converted the price in smallest unit
	 */
	public static int convertPrice(BigDecimal price) {
		return price.multiply(BigDecimal.valueOf(100)).intValue();
	}

	/**
	 * 金额，转换单位：分－>元 (convert back to our platform)
	 * 
	 * @param price the price in smallest unit
	 * @return converted BigDecimal standard unit our platform
	 */
	public static BigDecimal convertPrice(int price) {
		return BigDecimal.valueOf(price).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
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
