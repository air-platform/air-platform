package net.aircommunity.platform.service.internal.payment.newpay.client;

import java.util.Date;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.collect.ImmutableMap;

import io.micro.common.UUIDs;
import okhttp3.FormBody;
import okhttp3.RequestBody;

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

	// make accessible package private
	NewpayRefundRequest() {
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
		.append("&refundOrderID=").append(model.getRequestNo())
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
		requestId = newRequestId();
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
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NewpayRefundRequest [").append(buildParams()).append("]");
		return builder.toString();
	}
}
