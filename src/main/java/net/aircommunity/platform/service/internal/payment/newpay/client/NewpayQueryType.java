package net.aircommunity.platform.service.internal.payment.newpay.client;

/**
 * Newpay query type.
 * 
 * @author Bin.Zhang
 */
public enum NewpayQueryType {

	/**
	 * 支付订单
	 */
	PAYMENT("1"),

	/**
	 * 退款订单
	 */
	REFUND("2");

	private final String code;

	private NewpayQueryType(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public static NewpayQueryType fromString(String code) {
		for (NewpayQueryType state : values()) {
			if (state.code.equals(code)) {
				return state;
			}
		}
		return null;
	}

}
