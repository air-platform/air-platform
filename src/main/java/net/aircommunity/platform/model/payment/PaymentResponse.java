package net.aircommunity.platform.model.payment;

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

	public static final String MSG_SUCCESS = "success";
	public static final String MSG_FAILURE = "failure";

	private final Status status;

	// message will be written to output as response to the payment gateway
	private final String message;

	// useful message regarding this response for trace purpose
	private final String body;

	public static PaymentResponse failure(Status status, String message, String body) {
		return new PaymentResponse(status, message, body);
	}

	public static PaymentResponse failure(Status status) {
		return failure(status, null);
	}

	public static PaymentResponse failure(String message) {
		return failure(Status.UNKNOWN, message, null);
	}

	public static PaymentResponse failure(Status status, String body) {
		return failure(status, MSG_FAILURE, body);
	}

	public static PaymentResponse success() {
		return success(MSG_SUCCESS);
	}

	public static PaymentResponse success(String message) {
		return new PaymentResponse(Status.SUCCESS, message, null);
	}

	private PaymentResponse(Status status, String message, String body) {
		this.status = status;
		this.message = message;
		this.body = body;
	}

	public boolean isSuccessful() {
		return Status.SUCCESS == status;
	}

	public Status getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}

	public String getBody() {
		return body;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PaymentResponse [status=").append(status).append(", message=").append(message).append(", body=")
				.append(body).append("]");
		return builder.toString();
	}

	public enum Status {

		/**
		 * Success
		 */
		SUCCESS,

		INVALID_SIGNATURE,

		APP_ID_MISMATCH,

		/**
		 * The trade status is not success
		 */
		UNSUCCESS_TRADE_STATUS,

		/**
		 * Order not exists
		 */
		ORDER_NOT_FOUND,

		/**
		 * Not ready to PAID
		 */
		ORDER_ILLEGAL_STATUS,

		/**
		 * Order total amount mismatch
		 */
		TOTAL_AMOUNT_MISMATCH,

		/**
		 * Unknown status
		 */
		UNKNOWN;

		public int getHttpStatusCode() {
			return this == SUCCESS ? 200 : 599;
		}
	}
}
