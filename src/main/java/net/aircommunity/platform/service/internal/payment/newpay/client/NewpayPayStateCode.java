package net.aircommunity.platform.service.internal.payment.newpay.client;

/**
 * Payment state code of Newpay (支付交易状态码 (商户请以此字段唯一判断退款订单交易状态))
 * 
 * @author Bin.Zhang
 */
public enum NewpayPayStateCode {

	// Payment
	ACCEPTED("0"), IN_PROCESSING("1"), SUCCESS("2"), FAILURE("3");

	private final String code;

	private NewpayPayStateCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public static NewpayPayStateCode fromString(String code) {
		for (NewpayPayStateCode state : values()) {
			if (state.code.equals(code)) {
				return state;
			}
		}
		return null;
	}

}
