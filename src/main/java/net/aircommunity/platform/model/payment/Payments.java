package net.aircommunity.platform.model.payment;

/**
 * Payment constants and utilities.
 * 
 * @author Bin.Zhang
 */
public final class Payments {
	public static final String LOGGER_NAME = "net.aircommunity.platform.payment";

	public static final String PAYMENT_CONFIRMATION_STATUS = "status";

	// used in MDC
	public static final String KEY_PAYMENT_METHOD = "paymentMethod";
	public static final String KEY_PAYMENT_ACTION = "paymentAction";
	//
	public static final String ACTION_PAY_REQUEST = "PAY_REQUEST";
	public static final String ACTION_TRADE_SYNC = "TRADE_SYNC";
	public static final String ACTION_REFUND = "REFUND";
	public static final String ACTION_QUERY_REFUND = "QUERY_REFUND";
	public static final String ACTION_QUERY_PAYMENT = "QUERY_PAYMENT";
	public static final String ACTION_VERIFY_CLIENT_NOTIFY = "CLT_NOTIFY";
	public static final String ACTION_PROCESS_SERVER_NOTIFY = "SRV_NOTIFY";

	// TradeNo generated: orderNo_timestamp (29 in length)
	// Max tradeNo permitted by Wechat: 32
	// Max tradeNo permitted by Newpay: 32
	// Max tradeNo permitted by Alipay: 64
	private static final String OUT_TRADENO_SEPARATOR = "_";
	private static final String OUT_TRADENO_FORMAT = "%s" + OUT_TRADENO_SEPARATOR + "%s";

	/**
	 * Generate a tradeNo based on our orderNo. It may be used when pay or refund.
	 * 
	 * @param orderNo the orderNo of our platform
	 * @return generated tradeNo
	 */
	public static String generateTradeNo(String orderNo) {
		return String.format(OUT_TRADENO_FORMAT, orderNo, Long.toString(System.currentTimeMillis()));
	}

	/**
	 * Extract the orderNo from the generated tradeNo.
	 * 
	 * @param tradeNo the generated tradeNo
	 * @return orderNo
	 */
	public static String extractOrderNo(String tradeNo) {
		return tradeNo.split(OUT_TRADENO_SEPARATOR)[0];
	}

	private Payments() {
		throw new AssertionError();
	}
}
