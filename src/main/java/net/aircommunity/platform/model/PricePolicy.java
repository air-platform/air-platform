package net.aircommunity.platform.model;

/**
 * Price policy of a product.
 * 
 * @author Bin.Zhang
 */
public enum PricePolicy {

	/**
	 * Standard price (single price)
	 */
	STANDARD,

	/**
	 * Sales packages (multiple prices)
	 */
	SALES_PACKAGE,

	/**
	 * Per hour price
	 */
	PER_HOUR;

}
