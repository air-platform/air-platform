package net.aircommunity.platform.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Payment info of an order.
 * 
 * @author Bin.Zhang
 */
@Embeddable
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	// amount of payment
	@Column(name = "payment_amount")
	private double amount;

	// alipay, wechat etc.
	@Column(name = "payment_method")
	private String method;

	// trade No. of payment
	@Column(name = "payment_trade_no")
	private String tradeNo;

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

}
