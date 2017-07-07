package net.aircommunity.platform.service.internal.payment.newpay.client;

/**
 * Newpay Charset
 * 
 * @author Bin.Zhang
 */
public enum NewpayCharset {
	UTF8("1", "UTF-8");

	private final String code;
	private final String name;

	private NewpayCharset(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	public static NewpayCharset fromString(String code) {
		for (NewpayCharset status : values()) {
			if (status.code.equals(code)) {
				return status;
			}
		}
		return null;
	}
}
