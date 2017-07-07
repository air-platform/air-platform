package net.aircommunity.platform.service.internal.payment.newpay.client;

/**
 * Refund state code of Newpay (退款交易状态码 (商户请以此字段唯一判断退款订单交易状态))
 * 
 * @author Bin.Zhang
 */
public enum NewpayRefundStateCode {

	// Refund
	IN_PROCESSING("1"), SUCCESS("2"), FAILURE("3");

	private final String code;

	private NewpayRefundStateCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public static NewpayRefundStateCode fromString(String code) {
		for (NewpayRefundStateCode state : values()) {
			if (state.code.equals(code)) {
				return state;
			}
		}
		return null;
	}

}
