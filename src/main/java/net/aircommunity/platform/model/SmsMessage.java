package net.aircommunity.platform.model;

import java.io.Serializable;

/**
 * SMS Message
 * 
 * @author kxw
 */
public class SmsMessage implements Serializable {
	private static final long serialVersionUID = 1L;

	private String code;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SmsMessage [code=").append(code).append("]");
		return builder.toString();
	}
}
