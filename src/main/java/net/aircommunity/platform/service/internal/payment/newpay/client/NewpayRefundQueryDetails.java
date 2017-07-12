package net.aircommunity.platform.service.internal.payment.newpay.client;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.base.Splitter;

import net.aircommunity.platform.service.internal.payment.newpay.client.NewpayMessage.TimeAdapter;

/**
 * Newpay refund query details
 * 
 * @author Bin.Zhang
 */
public class NewpayRefundQueryDetails implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final Iterable<String> QUERY_DETAILS_PROPS = Splitter.on(",")
			.split("refundOrderID,orderID,refundAmount,refundTime,completeTime,refundNo,stateCode");

	// 商户退款订单 号 String(32) - 商户系统本次退款请求的订单号,必须保证唯一性 (每次都必须不同）
	@XmlElement(name = "refundOrderID", required = true)
	private String refundOrderId;

	// 商户 供的唯一订单号 String(32)
	@XmlElement(name = "orderID", required = true)
	private String orderNo;

	// 商户退款金额
	@XmlElement(name = "refundAmount", required = true)
	private int refundAmount;

	// 商户退款订单时间 String(14), yyyyMMddHHmmss, 20071117020101
	@XmlElement(name = "refundTime", required = true)
	@XmlJavaTypeAdapter(TimeAdapter.class)
	private Date refundTime;

	// 处理完成时间 String(14), yyyyMMddHHmmss, 20071117020101
	@XmlElement(name = "completeTime", required = true)
	@XmlJavaTypeAdapter(TimeAdapter.class)
	private Date completeTime;

	// 退款流水号 String(32) 新生支付平台生成的退款订单号,新生支付平台 受理商户退款订单请求失败时,无退款订单号
	@XmlElement(name = "refundNo", required = false)
	private String tradeNo;

	// 交易状态码 (不同业务意思不同)
	@XmlElement(name = "stateCode", required = true)
	private String stateCode;

	public String getRefundOrderId() {
		return refundOrderId;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public int getRefundAmount() {
		return refundAmount;
	}

	public Date getRefundTime() {
		return refundTime;
	}

	public Date getCompleteTime() {
		return completeTime;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public String getStateCode() {
		return stateCode;
	}

	public boolean isTradeSuccessful() {
		return NewpayRefundStateCode.SUCCESS.getCode().equals(stateCode);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NewpayRefundQueryDetails [refundOrderId=").append(refundOrderId).append(", orderNo=")
				.append(orderNo).append(", refundAmount=").append(refundAmount).append(", refundTime=")
				.append(refundTime).append(", completeTime=").append(completeTime).append(", tradeNo=").append(tradeNo)
				.append(", stateCode=").append(stateCode).append("]");
		return builder.toString();
	}
}
