package net.aircommunity.platform.service.internal.payment.newpay.client;

/**
 * Root exception for NewPay
 * 
 * @author Bin.Zhang
 */
public class NewpayException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public NewpayException() {
	}

	public NewpayException(String message) {
		super(message);
	}

	public NewpayException(Throwable cause) {
		super(cause);
	}

	public NewpayException(String message, Throwable cause) {
		super(message, cause);
	}

}
