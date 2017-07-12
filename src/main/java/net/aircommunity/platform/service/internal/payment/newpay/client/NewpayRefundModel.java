package net.aircommunity.platform.service.internal.payment.newpay.client;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Newpay refund model.
 * 
 * @author Bin.Zhang
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class NewpayRefundModel implements Serializable {
	private static final long serialVersionUID = 1L;

	// 商户原始订单号 String(32) - 商户系统原始支付请求的商户订单号
	@XmlElement(name = "orderID", required = true)
	private String orderNo;

	// 订单明细金额 String(18) (整数,以分为单位。 例如:10 元,金额为 1000)
	@XmlElement(name = "refundAmount", required = true)
	private int refundAmount;

	// not newpay prop (as refundOrderID)
	private String requestNo;

	// not newpay prop
	private String refundReason;

	// not newpay prop
	private boolean isPartialRefund;

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getRequestNo() {
		return requestNo;
	}

	public void setRequestNo(String requestNo) {
		this.requestNo = requestNo;
	}

	public int getRefundAmount() {
		return refundAmount;
	}

	public void setRefundAmount(int refundAmount) {
		this.refundAmount = refundAmount;
	}

	public String getRefundReason() {
		return refundReason;
	}

	public void setRefundReason(String refundReason) {
		this.refundReason = refundReason;
	}

	public boolean isPartialRefund() {
		return isPartialRefund;
	}

	public void setPartialRefund(boolean isPartialRefund) {
		this.isPartialRefund = isPartialRefund;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NewpayRefundModel [orderNo=").append(orderNo).append(", refundAmount=").append(refundAmount)
				.append(", requestNo=").append(requestNo).append(", refundReason=").append(refundReason)
				.append(", isPartialRefund=").append(isPartialRefund).append("]");
		return builder.toString();
	}
}
