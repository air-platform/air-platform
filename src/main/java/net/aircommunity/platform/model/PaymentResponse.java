package net.aircommunity.platform.model;

import javax.annotation.concurrent.Immutable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Payment response of an order to the payment system.
 * 
 * @author Bin.Zhang
 */
@Immutable
@XmlAccessorType(XmlAccessType.FIELD)
public final class PaymentResponse {

	// http status code
	private final int code;
	private final String body;

	public PaymentResponse(String body) {
		this(200, body);
	}

	public PaymentResponse(int code, String body) {
		this.code = code;
		this.body = body;
	}

	public int getCode() {
		return code;
	}

	public String getBody() {
		return body;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PaymentResponse [code=").append(code).append(", body=").append(body).append("]");
		return builder.toString();
	}
}
