package net.aircommunity.platform.common.math;

import java.math.BigDecimal;

/**
 * Math utilities
 * 
 * @author Bin.Zhang
 */
public final class Maths {

	/**
	 * Round value with scale
	 * 
	 * @param value the value
	 * @param scale the scale
	 * @param roundingMode the roundingMode
	 * @return round result
	 * @see #ROUND_UP
	 * @see #ROUND_DOWN
	 * @see #ROUND_CEILING
	 * @see #ROUND_FLOOR
	 * @see #ROUND_HALF_UP
	 * @see #ROUND_HALF_DOWN
	 * @see #ROUND_HALF_EVEN
	 * @see #ROUND_UNNECESSARY
	 */
	public static double round(double value, int scale, int roundingMode) {
		BigDecimal bd = new BigDecimal(value).setScale(scale, roundingMode);
		return bd.doubleValue();
	}

	private Maths() {
		throw new AssertionError();
	}

	public static void main(String arg[]) {
		double v = 4353.3535353534242;
		System.out.println(round(v, 2, BigDecimal.ROUND_HALF_UP));
	}

}
