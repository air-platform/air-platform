package net.aircommunity.platform.model;

import java.util.Locale;

import net.aircommunity.platform.model.domain.Product.Category;

/**
 * Member Point Rules.
 * 
 * @author Bin.Zhang
 */
public final class PointRules {

	/**
	 * The default point earned for account registration
	 */
	public static final int DEFAULT_ACCOUNT_REGISTRATION_POINTS = 100;

	/**
	 * The default exchange rate for points to RMB, 10 means 10 points for 1 RMB
	 */
	public static final int DEFAULT_EXCHANGE_RATE = 10;

	/**
	 * The default exchange percent, the max points can be used for an order, 50 means 50% of the order total price.
	 */
	public static final int DEFAULT_EXCHANGE_PERCENT = 50;

	/**
	 * First user order price off in RMB, 0 means no price off
	 */
	public static final int DEFAULT_FIRST_ORDER_PRICE_OFF = 0;

	/**
	 * Account registration
	 */
	public static final String ACCOUNT_REGISTRATION = "account_registration";

	/**
	 * First order price off
	 */
	public static final String FIRST_ORDER_PRICE_OFF = "order_first_price_off";

	/**
	 * Order finished, points will get when order is finished (prefix+product category)
	 */
	public static final String ORDER_FINISHED_PREFIX = "order_finished_";

	/**
	 * Daily signin, points will get on daily success
	 */
	public static final String DAILY_SIGNIN_PREFIX = "daily_signin_";

	/**
	 * Return order finished rule name for this product category.
	 * 
	 * @return rule name
	 */
	public static String getOrderFinishedRule(Category category) {
		return forName(ORDER_FINISHED_PREFIX, category.name());
	}

	/**
	 * Return daily signin rule name.
	 * 
	 * @return rule name
	 */
	public static String getDailySigninRule(int sigins) {
		return forName(DAILY_SIGNIN_PREFIX, sigins);
	}

	private static String forName(String prefix, Object name) {
		return new StringBuilder().append(prefix).append(name).toString().toLowerCase(Locale.ENGLISH);
	}

}
