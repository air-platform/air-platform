package net.aircommunity.platform.service.internal.payment.newpay;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;

import io.micro.common.UUIDs;
import okhttp3.ConnectionPool;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Refund request.
 * 
 * @author Bin.Zhang
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class NewpayRefundRequest extends NewpayRequest {
	private static final long serialVersionUID = 1L;

	// 商户退款订单 号 String(32) - 商户系统本次退款请求的订单号,必须保证唯一性 (每次都必须不同）
	@XmlElement(name = "refundOrderID", required = true)
	private String refundOrderId;

	// keep it for reference
	// 商户原始订单号 String(32) - 商户系统原始支付请求的商户订单号
	// @XmlElement(name = "orderID", required = true)
	// private String orderNo;

	// 商户通知地址 String(256) - 通知商户处理结果的异步通知地址
	@XmlElement(name = "noticeUrl", required = true)
	private String notifyUrl;

	// 扩展字段 String(256) 填写英文或中文字符串,照原样返回给商户
	@XmlElement(name = "remark", required = false)
	private String remark = "refund";

	// 退款目的地类型 String(1) - 退款目的地的类型 1:原路退回
	@XmlElement(name = "destType", required = true)
	private String destType = "1";

	// 退款类型 String(1) - 退款类型 1:全额退款 2:部分退款
	@XmlElement(name = "refundType", required = true)
	private NewpayRefundType refundType = NewpayRefundType.FULL;

	// keep it for reference
	// 订单明细金额 String(18) (整数,以分为单位。 例如:10 元,金额为 1000)
	// @XmlElement(name = "refundAmount", required = true)
	// private int refundAmount;

	// 商户退款订单 时间 String(14) 商户交的退款订单时间, e.g. 20071117020101
	@XmlElement(name = "refundTime", required = true)
	@XmlJavaTypeAdapter(TimeAdapter.class)
	private Date refundTime;

	private NewpayRefundModel model;

	public String getRefundOrderId() {
		return refundOrderId;
	}

	public void setRefundOrderId(String refundOrderId) {
		this.refundOrderId = refundOrderId;
	}

	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = notNullOrEmpty(remark, "remark is required");
	}

	public String getDestType() {
		return destType;
	}

	public void setDestType(String destType) {
		this.destType = destType;
	}

	public NewpayRefundType getRefundType() {
		return refundType;
	}

	public void setRefundType(NewpayRefundType refundType) {
		this.refundType = refundType;
	}

	public Date getRefundTime() {
		return refundTime;
	}

	public void setRefundTime(Date refundTime) {
		this.refundTime = refundTime;
	}

	public NewpayRefundModel getModel() {
		return model;
	}

	public void setModel(NewpayRefundModel model) {
		this.model = model;
	}

	private String buildSignMsg() {
		// @formatter:off
		return new StringBuilder()
		.append("version=").append(version)
		.append("&serialID=").append(requestId)
		.append("&refundOrderID=").append(refundOrderId)
		.append("&orderID=").append(model.getOrderNo())
		.append("&destType=").append(destType)
		.append("&refundType=").append(refundType.getCode())
		.append("&refundAmount=").append(model.getRefundAmount())
		.append("&refundTime=").append(formatTimestamp(refundTime))
		.append("&noticeUrl=").append(notifyUrl)
		.append("&partnerID=").append(partnerId)
		.append("&remark=").append(remark)
		.append("&charset=").append(charset)
		.append("&signType=").append(signType)
		.toString();
		// @formatter:on
	}

	private Map<String, String> buildParams() {
		refundOrderId = UUIDs.shortTimebased();
		refundTime = new Date();
		String signMsg = buildSignMsg();
		sign = signRsaSignature(signMsg, NewpayCharset.fromString(charset));
		Map<String, String> result = convertQueryString(signMsg);
		return ImmutableMap.<String, String> builder().putAll(result).put("signMsg", sign).build();
	}

	RequestBody build() {
		Map<String, String> params = buildParams();
		FormBody.Builder builder = new FormBody.Builder();
		params.entrySet().stream().forEach(e -> {
			builder.add(e.getKey(), e.getValue());
		});
		return builder.build();
		// @formatter:off
		/*
		 return new FormBody.Builder()
		 .add("version",version)
		 .add("serialID",requestId)
		 .add("refundOrderID", refundOrderId)
		 .add("orderID", model.getOrderNo())
		 .add("destType", destType)
		 .add("refundType", refundType)
		 .add("refundAmount", Integer.toString(model.getRefundAmount()))
		 .add("refundTime", TIME_FORMATTER.format(refundTime))
		 .add("noticeUrl", notifyUrl)
		 .add("partnerID", partnerId)
		 .add("remark", remark)
		 .add("charset", charset)
		 .add("signType", signType)
		 .add("signMsg", signRsaSignature(buildSignMsg(), NewpayCharset.fromString(charset)))
	    .build();
	    */
		// @formatter:on
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NewpayRefundRequest [").append(buildParams()).append("]");
		return builder.toString();
	}

	public static void main(String[] args) throws Exception {
		String version = "2.6";
		String requestId = "8b3e1b40557411e78dd9b0ffdc6678c5";
		String refundOrderID = "8b3e1b40557411e78dd9b0ffdc6678c0";
		// String orderID="0123456789";
		String orderID = "C2BBC50B3C00000";
		String destType = "1";
		String refundType = "1";
		String refundAmount = "1";
		String refundTime = "20170620150000";
		String noticeUrl = "http://innertest.aircommunity.cn/api/v2/payment/newpay/notify";
		String partnerID = "11000002981";
		String remark = "test";
		String charset = "1";
		String signType = "1";
		String msg = new StringBuilder().append("version=").append(version).append("&serialID=").append(requestId)
				.append("&refundOrderID=").append(refundOrderID).append("&orderID=").append(orderID)
				.append("&destType=").append(destType).append("&refundType=").append(refundType)
				.append("&refundAmount=").append(refundAmount).append("&refundTime=").append(refundTime)
				.append("&noticeUrl=").append(noticeUrl).append("&partnerID=").append(partnerID).append("&remark=")
				.append(remark).append("&charset=").append(charset).append("&signType=").append(signType).toString();

		NewpayConfig config = new NewpayConfig();
		config.setCertAlias("hnapaySH");
		config.setCertPassword("d0M7WZ");
		config.setGatewayPublicKey(
				"30819f300d06092a864886f70d010101050003818d00308189028181009fdb5cc9a3de547fd28a3cbc5a82acda4fe2f47efb0ab8b1b9716e6bcf31cac207def13914dbf6672364f40e8c11bf3ef0f7c91f2812b1bb4abf555f10576d548bf03139775fadb40443f415497b45f0db42a5a5ea71239d35017d743369c7f56b0e969aaefeb1a7fe277db78095ffade8875491fa3c473d0d7b97e2869b12470203010001");
		config.setKeystorePath("config/newpay-rsa.jks");
		config.setKeystorePassword("jSB3vr");
		// config.setNotifyUrl("http://innertest.aircommunity.cn/api/v2/payment/newpay/notify");
		config.setPartnerId("11000002981");
		// config.setReturnUrl("http://innertest.aircommunity.cn/api/v2/payment/newpay/return");
		NewpaySignature signature = new NewpaySignature(config);
		String signMsg = signature.signWithRSA(msg, NewpayCharset.UTF8);
		System.out.println(signMsg);

		ConnectionPool connectionPool = new ConnectionPool(5, 5, TimeUnit.SECONDS);
		OkHttpClient httpClient = new OkHttpClient.Builder().readTimeout(6000, TimeUnit.MILLISECONDS)
				.connectTimeout(6000, TimeUnit.MILLISECONDS).connectionPool(connectionPool)
				.retryOnConnectionFailure(false).build();

		// refundOrderID=8b3e1b40557411e78dd9b0ffdc6678c2&resultCode=0311&stateCode=7&orderID=0123456789&refundAmount=100&refundTime=20170620130000&completeTime=20170620140856&refundNo=&partnerID=11000002981&remark=test&charset=1&signType=1&signMsg=49d08cc69bf30e753da3dce3a8bc962b5941e7c8000484e0a466477faaa39b7f4a1567354c02dfc6006f14dcbe431ddd53ceca9ecd9b99c184d6884ba758e6370ab1bbdd7a706860ee11062265c1fba96473375049d0c390a53a9ebf4b63331d0be1f97ad0598923f85a7b15ad9cbd170f87c89c583efaf7e594f80017a4b22a
		// @formatter:off
		RequestBody formBody = new FormBody.Builder()
				 .add("version",version)
				 .add("serialID",requestId)
				 .add("refundOrderID",refundOrderID)
				 .add("orderID",orderID)
				 .add("destType",destType)
				 .add("refundType",refundType)
				 .add("refundAmount",refundAmount)
				 .add("refundTime",refundTime)
				 .add("noticeUrl",noticeUrl)
				 .add("partnerID",partnerID)
				 .add("remark",remark)
				 .add("charset",charset)
				 .add("signType",signType)
				 .add("signMsg",signMsg)
			    .build();
		// @formatter:on
		// RequestBody body=RequestBody.create(MediaType.APPLICATION_FORM_URLENCODED, formBody);
		Request request = new Request.Builder().url("https://gateway.hnapay.com/website/refund.htm").post(formBody)
				.build();
		Response response = httpClient.newCall(request).execute();
		String body = response.body().string();
		System.out.println(body);
		String s = "refundOrderID=8b3e1b40557411e78dd9b0ffdc6678c0&resultCode=0000&stateCode=1&orderID=C2BBC50B3C00000&refundAmount=1&refundTime=20170620150000&completeTime=20170620154301&refundNo=2017062082055548&partnerID=11000002981&remark=test&charset=1&signType=1&signMsg=44d1f240533de7e20acfe8f9a9a5b7803b6a6a8e60f31e590a7897d82de2021d461361ed5fc0110a56af967fb36ec1661ce53316abd1f8e9c052563962956858a625149f960f1dca087fae38bf537090ebc96f5fed10c0301018cbe00229c2f2acabf765302f19f626a9e079bee1724f2b062d481036bc3964d0a00b5da46d39";
		Map<String, String> p = Splitter.on("&").withKeyValueSeparator("=").split(s);
		System.out.println(p);
		System.out.println(NewpayRefundResponse.parse(p));
	}

}
