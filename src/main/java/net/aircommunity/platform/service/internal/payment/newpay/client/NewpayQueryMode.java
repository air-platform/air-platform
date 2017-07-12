package net.aircommunity.platform.service.internal.payment.newpay.client;

/**
 * Newpay query mode.
 * 
 * @author Bin.Zhang
 */
public enum NewpayQueryMode {

	/**
	 * 单笔
	 */
	SINGLE("1"),

	/**
	 * 批量
	 */
	BATCH("2");

	private final String code;

	private NewpayQueryMode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public static NewpayQueryMode fromString(String code) {
		for (NewpayQueryMode state : values()) {
			if (state.code.equals(code)) {
				return state;
			}
		}
		return null;
	}

}
