package net.aircommunity.platform.model.payment;

import java.io.Serializable;

import javax.annotation.concurrent.Immutable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * The payment request info of an order for a payment system. (e.g. Alipay, Wechat etc.)
 * 
 * @author Bin.Zhang
 */
@Immutable
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	// order payment string
	// e.g. alipay: url encoded full ordering string
	// e.g. wechat: xml unified ordering string
	// e.g. newpay: json sign json order request
	private final Object paymentInfo;

	public PaymentRequest(Object paymentInfo) {
		this.paymentInfo = paymentInfo;
	}

	public Object getPaymentInfo() {
		return paymentInfo;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OrderPaymentInfo [paymentInfo=").append(paymentInfo).append("]");
		return builder.toString();
	}
}
