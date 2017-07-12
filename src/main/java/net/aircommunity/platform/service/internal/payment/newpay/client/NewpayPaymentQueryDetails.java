package net.aircommunity.platform.service.internal.payment.newpay.client;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.base.Splitter;

import net.aircommunity.platform.service.internal.payment.newpay.client.NewpayMessage.TimeAdapter;

/**
 * Newpay payment query details
 * 
 * @author Bin.Zhang
 */
public class NewpayPaymentQueryDetails implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final Iterable<String> QUERY_DETAILS_PROPS = Splitter.on(",")
			.split("orderID,orderAmount,payAmount,acquiringTime,completeTime,orderNo,stateCode");

	// 商户 供的唯一订单号 String(32)
	@XmlElement(name = "orderID", required = true)
	private String orderNo;

	// String(15)
	// 商户订单金额
	@XmlElement(name = "orderAmount", required = true)
	private int orderAmount;

	// String(15)
	// 实际支付金额
	@XmlElement(name = "payAmount", required = true)
	private int payAmount;

	// 订单提交时间 String(14), yyyyMMddHHmmss, 20071117020101
	@XmlElement(name = "acquiringTime", required = true)
	@XmlJavaTypeAdapter(TimeAdapter.class)
	private Date acquiringTime;

	// 处理完成时间 String(14), yyyyMMddHHmmss, 20071117020101
	@XmlElement(name = "completeTime", required = true)
	@XmlJavaTypeAdapter(TimeAdapter.class)
	private Date completeTime;

	// 支付订单号 String(32) 新生支付平台生成的唯一支付订单号
	@XmlElement(name = "orderNo", required = false)
	private String tradeNo;

	// 交易状态码 (不同业务意思不同)
	@XmlElement(name = "stateCode", required = true)
	private String stateCode;

	public String getOrderNo() {
		return orderNo;
	}

	public int getOrderAmount() {
		return orderAmount;
	}

	public int getPayAmount() {
		return payAmount;
	}

	public Date getAcquiringTime() {
		return acquiringTime;
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
		return NewpayPayStateCode.SUCCESS.getCode().equals(stateCode);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NewpayPaymentQueryDetails [orderNo=").append(orderNo).append(", orderAmount=")
				.append(orderAmount).append(", payAmount=").append(payAmount).append(", acquiringTime=")
				.append(acquiringTime).append(", completeTime=").append(completeTime).append(", tradeNo=")
				.append(tradeNo).append(", stateCode=").append(stateCode).append("]");
		return builder.toString();
	}
}
