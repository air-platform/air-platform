package net.aircommunity.platform.service.internal.payment.newpay.client;

import java.util.Date;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Newpay pay response (async notification from server)
 * 
 * @author Bin.Zhang
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class NewpayPayResponse extends NewpayResponse {
	private static final long serialVersionUID = 1L;

	// 商户 供的唯一订单号 String(32)
	@XmlElement(name = "orderID", required = true)
	private String orderNo;

	// 交易状态码 (商户请以此字段唯一判断订单交易状态。例如:交易状态码为 2,则表示订单 状态成功)
	// 0:已接受 1:处理中 2:交易成功 3:交易失败
	@XmlElement(name = "stateCode", required = true)
	private String stateCode;

	// String(18)
	// 商户订单金额
	@XmlElement(name = "orderAmount", required = true)
	private int orderAmount;

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

	/**
	 * Parse the data to construct NewpayPayResponse (signature is not verified)
	 * 
	 * @param data the incoming key value data
	 * @return NewpayPayResponse
	 */
	public static NewpayPayResponse parse(Map<String, String> data) {
		return convert(data, NewpayPayResponse.class);
	}

	public String getOrderNo() {
		return orderNo;
	}

	public String getStateCode() {
		return stateCode;
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

	public boolean isTradeSuccessful() {
		return NewpayPayStateCode.SUCCESS.getCode().equals(stateCode);
	}

	private String buildSignMsg() {
		// @formatter:off
		return new StringBuilder()
		 .append("orderID=").append(orderNo)
		 .append("&resultCode=").append(resultCode)
		 .append("&stateCode=").append(stateCode)
		 .append("&orderAmount=").append(orderAmount)
		 .append("&payAmount=").append(payAmount)
		 .append("&acquiringTime=").append(formatTimestamp(acquiringTime))
		 .append("&completeTime=").append(formatTimestamp(completeTime))
		 .append("&orderNo=").append(tradeNo)
		 .append("&partnerID=").append(partnerId)
		 .append("&remark=").append(remark)
		 .append("&charset=").append(charset)
		 .append("&signType=").append(signType)
		 .toString();
		// @formatter:on
	}

	@Override
	public void verifySignature() {
		String signMsg = buildSignMsg();
		verifyRsaSignature(signMsg, sign, NewpayCharset.fromString(charset));
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NewpayPayResponse [orderNo=").append(orderNo).append(", resultCode=").append(resultCode)
				.append(", stateCode=").append(stateCode).append(", orderAmount=").append(orderAmount)
				.append(", payAmount=").append(payAmount).append(", acquiringTime=")
				.append(formatTimestamp(acquiringTime)).append(", completeTime=").append(formatTimestamp(completeTime))
				.append(", tradeNo=").append(tradeNo).append(", partnerId=").append(partnerId).append(", remark=")
				.append(remark).append(", charset=").append(charset).append(", signType=").append(signType)
				.append(", sign=").append(sign).append("]");
		return builder.toString();
	}
}
