package net.aircommunity.platform.model.payment;

import java.io.Serializable;

import javax.annotation.concurrent.Immutable;

/**
 * Payment notification from client or server. (e.g. Alipay)
 * 
 * @author Bin.Zhang
 */
@Immutable
public class PaymentNotification implements Serializable {
	private static final long serialVersionUID = 1L;

	private final Object data;

	public PaymentNotification(Object data) {
		this.data = data;
	}

	public Object getData() {
		return data;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PaymentNotification [data=").append(data).append("]");
		return builder.toString();
	}

}
