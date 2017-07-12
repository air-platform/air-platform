package net.aircommunity.platform.service.internal.payment.newpay.client;

import java.util.Date;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * NewpayRefund Response (async notification from server)
 * 
 * @author Bin.Zhang
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class NewpayRefundResponse extends NewpayResponse {
	private static final long serialVersionUID = 1L;

	// 商户 供的唯一订单号 String(32)
	@XmlElement(name = "orderID", required = true)
	private String orderNo;

	// 交易状态码 (不同业务意思不同)
	@XmlElement(name = "stateCode", required = true)
	private String stateCode;

	// 商户退款订单号 String(32)
	@XmlElement(name = "refundOrderID", required = true)
	private String refundOrderId;

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

	/**
	 * Parse the data to construct NewpayRefundResponse (signature is not verified)
	 * 
	 * @param data the incoming key value data
	 * @return NewpayRefundResponse
	 */
	public static NewpayRefundResponse parse(Map<String, String> data) {
		return convert(data, NewpayRefundResponse.class);
	}

	public String getOrderNo() {
		return orderNo;
	}

	public String getStateCode() {
		return stateCode;
	}

	public String getRefundOrderId() {
		return refundOrderId;
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

	public boolean isTradeSuccessful() {
		return NewpayRefundStateCode.SUCCESS.getCode().equals(stateCode);
	}

	private String buildSignMsg() {
		// @formatter:off
		return new StringBuilder()
		 .append("refundOrderID=").append(refundOrderId)
		 .append("&resultCode=").append(resultCode)
		 .append("&stateCode=").append(stateCode)
		 .append("&orderID=").append(orderNo)
		 .append("&refundAmount=").append(refundAmount)
		 .append("&refundTime=").append(TIME_FORMATTER.format(refundTime))
		 .append("&completeTime=").append(TIME_FORMATTER.format(completeTime))
		 .append("&refundNo=").append(tradeNo)
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
		builder.append("NewpayRefundResponse [refundOrderId=").append(refundOrderId).append(", refundAmount=")
				.append(refundAmount).append(", refundTime=").append(refundTime).append(", completeTime=")
				.append(completeTime).append(", tradeNo=").append(tradeNo).append(", orderNo=").append(orderNo)
				.append(", resultCode=").append(resultCode).append(", stateCode=").append(stateCode)
				.append(", resultMessage=").append(getResultMessage()).append(", partnerId=").append(partnerId)
				.append(", remark=").append(remark).append(", charset=").append(charset).append(", signType=")
				.append(signType).append(", sign=").append(sign).append("]");
		return builder.toString();
	}
}
