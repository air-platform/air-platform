package net.aircommunity.platform.service.internal.payment.newpay;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * TradePayModel
 * 
 * @author Bin.Zhang
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class NewpayPayModel implements Serializable {
	private static final long serialVersionUID = 1L;

	// 商户 供的唯一订单号 String(32)
	@XmlElement(name = "orderID", required = true)
	private String orderNo;

	// 订单明细金额 String(18) (整数,以分为单位。 例如:10 元,金额为 1000)
	@XmlElement(name = "orderAmount", required = true)
	private int totalAmount;

	// 商户名称 String(128)
	@XmlElement(name = "displayName", required = true)
	private String parterName = "";

	// 商品名称 String(256)
	@XmlElement(name = "goodsName", required = true)
	private String body;

	// 商品数量 String(15)
	@XmlElement(name = "goodsCount", required = true)
	private int quantity;

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public int getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(int totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getParterName() {
		return parterName;
	}

	public void setParterName(String parterName) {
		this.parterName = parterName;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String toOrderDetails() {
		// orderDetails=orderID,orderAmount,displayName,goodsName,goodsCount
		return new StringBuilder().append(orderNo).append(",").append(totalAmount).append(",")
				.append(parterName == null ? "" : replaceReservedFuckingChars(parterName)).append(",")
				.append(replaceReservedFuckingChars(body)).append(",").append(quantity).toString();
	}

	private String replaceReservedFuckingChars(String str) {
		// comma is used by separator of order details, WTF! REDICULOUS!
		return str.replace(",", "，");
	}

	@Override
	public String toString() {
		return toOrderDetails();
	}
}
