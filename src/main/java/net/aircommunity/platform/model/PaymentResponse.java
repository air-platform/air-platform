package net.aircommunity.platform.model;

/**
 * Payment response of an order to the payment system.
 * 
 * @author Bin.Zhang
 */
public final class PaymentResponse {

	private final String body;

	public PaymentResponse(String body) {
		this.body = body;
	}

	public String getBody() {
		return body;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PaymentResponse [body=").append(body).append("]");
		return builder.toString();
	}
}
