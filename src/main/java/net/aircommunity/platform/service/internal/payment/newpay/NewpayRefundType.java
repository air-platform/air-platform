package net.aircommunity.platform.service.internal.payment.newpay;

/**
 * Newpay refund type.
 * 
 * @author Bin.Zhang
 */
public enum NewpayRefundType {

	FULL("1"), PARTIAL("2");

	private final String code;

	private NewpayRefundType(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public static NewpayRefundType fromString(String type) {
		for (NewpayRefundType t : values()) {
			if (t.code.equals(type)) {
				return t;
			}
		}
		return null;
	}

}
