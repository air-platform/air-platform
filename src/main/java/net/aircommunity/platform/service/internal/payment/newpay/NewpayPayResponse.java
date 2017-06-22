package net.aircommunity.platform.service.internal.payment.newpay;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.micro.support.ObjectMappers;

/**
 * NewpayPay Response (async notification from server)
 * 
 * @author Bin.Zhang
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class NewpayPayResponse extends NewpayResponse {
	private static final long serialVersionUID = 1L;

	// Derived from parent, just make some comments
	// 交易状态码 (商户请以此字段唯一判断订单交易状态。例如:交易状态码为 2,则表示订单 状态成功)
	// 0:已接受 1:处理中 2:交易成功 3:交易失败
	// @XmlElement(name = "stateCode", required = true)
	/// private String stateCode;

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
	 * Parse the data to construct NewpayPayResponse
	 * 
	 * @param data the incoming key value data
	 * @return NewpayPayResponse
	 * @throws NewpayException if failed to parse or RSA signature verification failure
	 */
	public static NewpayPayResponse parse(Map<String, String> data) {
		NewpayPayResponse response = convert(data, NewpayPayResponse.class);
		response.verifySignature();
		return response;
	}

	public int getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(int orderAmount) {
		this.orderAmount = orderAmount;
	}

	public int getPayAmount() {
		return payAmount;
	}

	public void setPayAmount(int payAmount) {
		this.payAmount = payAmount;
	}

	public Date getAcquiringTime() {
		return acquiringTime;
	}

	public void setAcquiringTime(Date acquiringTime) {
		this.acquiringTime = acquiringTime;
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

	public static void main(String argp[]) throws Exception {
		Map<String, String> data = new HashMap<>();
		data.put("charset", "1");
		data.put("orderNo", "2017062082050124");
		data.put("frontMerUrl", "http://innertest.aircommunity.cn/api/v2/payment/newpay/return");
		data.put("orderID", "0123456789");
		data.put("resultCode", "0000");
		data.put("completeTime", "20170620094207");
		data.put("acquiringTime", "20170620093302");
		data.put("remark", "");
		data.put("orderAmount", "100");
		data.put("payAmount", "100");
		data.put("signType", "1");
		data.put("stateCode", "2");
		data.put("partnerID", "11000002981");
		data.put("signMsg",
				"2724bb98f4c457aa406a0981038f421e88cd2ddd02e79b2aa34bcf47bf16565defab22e8ce538ee795aadf6ed9643c7d885702e8a738716d1059ae5d80d821f5f0257ed01dc502665cb7e258a7fa1bd0f836489739c2ede0f6aad22b6d668d4526f6b87d80a1660f5ab261e16a3d7654542d5d6a8dcf7266dc9f4f4b2e8d5902");
		ObjectMapper mapper = ObjectMappers.getObjectMapper();
		String str = mapper.writeValueAsString(data);
		NewpayPayResponse response = mapper.readValue(str, NewpayPayResponse.class);
		System.out.println(response);
		//
		// String signMsg = "orderID=" + response.getOrderNo() + "&resultCode=" + response.getResultCode() +
		// "&stateCode="
		// + response.getStateCode() + "&orderAmount=" + response.getOrderAmount() + "&payAmount="
		// + response.getPayAmount() + "&acquiringTime=" + response.getAcquiringTime() + "&completeTime="
		// + response.getCompleteTime() + "&orderNo=" + response.getTradeNo() + "&partnerID="
		// + response.getPartnerId() + "&remark=" + response.getRemark() + "&charset=" + response.getCharset()
		// + "&signType=" + response.getSignType();

		// NewpayConfig config = new NewpayConfig();
		// config.setCertAlias("hnapaySH");
		// config.setCertPassword("d0M7WZ");
		// config.setGatewayPublicKey(
		// "30819f300d06092a864886f70d010101050003818d00308189028181009fdb5cc9a3de547fd28a3cbc5a82acda4fe2f47efb0ab8b1b9716e6bcf31cac207def13914dbf6672364f40e8c11bf3ef0f7c91f2812b1bb4abf555f10576d548bf03139775fadb40443f415497b45f0db42a5a5ea71239d35017d743369c7f56b0e969aaefeb1a7fe277db78095ffade8875491fa3c473d0d7b97e2869b12470203010001");
		// config.setKeystorePath("config/newpay-rsa.jks");
		// config.setKeystorePassword("jSB3vr");
		// config.setNotifyUrl("http://innertest.aircommunity.cn/api/v2/payment/newpay/notify");
		// config.setPartnerId("11000002981");
		// config.setReturnUrl("http://innertest.aircommunity.cn/api/v2/payment/newpay/return");
		// NewpaySignature signature = new NewpaySignature(config);
		// String sign = signature.signWithRSA(signMsg, NewpayCharset.fromString(response.getCharset()));
		// boolean r = signature.verifyRsaSignature(signMsg, response.getSign(),
		// NewpayCharset.fromString(response.getCharset()));
		// new NewpayResponse().verifyRsaSignature(signMsg, response.getSign(),
		// NewpayCharset.fromString(response.getCharset()));
		// System.out.println(signMsg);
		// System.out.println(sign);
		// System.out.println(r);

		NewpayPayResponse res = NewpayPayResponse.parse(data);
		System.out.println(res);
	}

}
