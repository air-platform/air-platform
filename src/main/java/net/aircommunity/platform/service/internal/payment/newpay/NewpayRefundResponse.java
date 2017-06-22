package net.aircommunity.platform.service.internal.payment.newpay;

import java.util.Date;
import java.util.HashMap;
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

	// Derived from parent, just make some comments
	// 退款交易状态码 (商户请以此字段唯一判断退款订单交易状态)
	// 1:退款处理中 2:退款成功 3:退款失败
	// @XmlElement(name = "stateCode", required = true)
	/// private String stateCode;

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
	 * Parse the data to construct NewpayRefundResponse
	 * 
	 * @param data the incoming key value data
	 * @return NewpayRefundResponse
	 * @throws NewpayException if failed to parse or RSA signature verification failure
	 */
	public static NewpayRefundResponse parse(Map<String, String> data) {
		NewpayRefundResponse response = convert(data, NewpayRefundResponse.class);
		response.verifySignature();
		return response;
	}

	public String getRefundOrderId() {
		return refundOrderId;
	}

	public void setRefundOrderId(String refundOrderId) {
		this.refundOrderId = refundOrderId;
	}

	public int getRefundAmount() {
		return refundAmount;
	}

	public void setRefundAmount(int refundAmount) {
		this.refundAmount = refundAmount;
	}

	public Date getRefundTime() {
		return refundTime;
	}

	public void setRefundTime(Date refundTime) {
		this.refundTime = refundTime;
	}

	public Date getCompleteTime() {
		return completeTime;
	}

	public void setCompleteTime(Date completeTime) {
		this.completeTime = completeTime;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
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

	public static void main(String argp[]) throws Exception {
		Map<String, String> data = new HashMap<>();
		data.put("orderID", "0123456789");
		data.put("refundTime", "20170620130000");
		data.put("refundOrderID", "8b3e1b40557411e78dd9b0ffdc6678c5");
		data.put("completeTime", "20170621091800");
		data.put("refundNo", "2017062082053529");
		data.put("refundAmount", "100");

		data.put("resultCode", "0000");
		data.put("stateCode", "2");
		data.put("remark", "test");
		data.put("charset", "1");
		data.put("signType", "1");
		data.put("partnerID", "11000002981");
		data.put("signMsg",
				"113731b5a2e5fda82c434ad9da97a2e0638f4e0e67c62930e283977b2edeb626766370545fba89ef8fef8fae51d201204726bf90a4f29bc387ebdda9d06ce25cba6c91fd947330c4ef1913aefc334b1a92814a0a0d90fe3bad20c96bfa474712302888a0298bd6c92f041511992f80191b6d8257a193329426a11427b0f52183");
		NewpayRefundResponse res = NewpayRefundResponse.parse(data);
		System.out.println(res);
	}

}
