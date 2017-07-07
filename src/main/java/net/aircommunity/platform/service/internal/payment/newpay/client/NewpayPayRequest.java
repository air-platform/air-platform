package net.aircommunity.platform.service.internal.payment.newpay.client;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * NewpayPay Request
 * 
 * @author Bin.Zhang
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class NewpayPayRequest extends NewpayRequest {
	private static final long serialVersionUID = 1L;

	// 订单提交时间 String(14), yyyyMMddHHmmss
	@XmlElement(name = "submitTime", required = true)
	@XmlJavaTypeAdapter(TimeAdapter.class)
	private Date submitTime;

	// 订单失效时间 String(14), yyyyMMddHHmmss
	@XmlElement(name = "failureTime", required = false)
	@XmlJavaTypeAdapter(TimeAdapter.class)
	private Date failureTime;

	// 客户下单域名 及IP String(128) - 商户系统域名和客户下单 IP 例如:www.taoXX.com[192.168.1.2]
	@XmlElement(name = "customerIP", required = false)
	private String customerIpAddress = "";

	// 订单明细信息 String(1024)
	@XmlElement(name = "orderDetails", required = true)
	private String orderDetails;

	// String(18)
	// 订单总金额,必须等于订单明细信息中明细项金 额总和,单位为分
	@XmlElement(name = "totalAmount", required = true)
	private int totalAmount;

	// 交易类型 String(4) 固定值:1000(默认)
	@XmlElement(name = "type", required = true)
	private String type = "1000";

	// 付款方新生账户号 String(64) (商户指定付款方的新生支付登录标识(通常为手 机或者 Email),该字段暂未使用。)
	@XmlElement(name = "buyerMarked", required = false)
	private String buyerMarked = "";

	// 付款方支付方式 String(128)
	// ALL:商户签约后开通的所有付款方式(非直连时: 默认值; 直连时:不可填 ALL)
	@XmlElement(name = "payType", required = true)
	private String payType = "ALL";

	// 目标资金机构代码 String(12)
	@XmlElement(name = "orgCode", required = false)
	private String orgCode = "";

	// 交易币种 String(1) - 传递的交易币种代码
	// 1:人民币(默认)
	// 2:预付卡(选择用预付费卡支付时,可选)
	// 3:授信额度
	@XmlElement(name = "currencyCode", required = false)
	private String currencyCode = "1";

	// String(1) 是否使用直连银行模式 0:非直连 (默认) 1:直连
	@XmlElement(name = "directFlag", required = false)
	private String directFlag = "0";

	// 资金来源借贷 String(1) 该字段为预留字段 0:无特殊要求(默认) 1:只借记 2:只贷记
	@XmlElement(name = "borrowingMarked", required = false)
	private String borrowingMarked = "0";

	// 优惠券标识 String(2) 是否可以使用新生 供的优惠券 1:可用 (默认) 0:不可用
	@XmlElement(name = "couponFlag", required = false)
	private String couponFlag = "0";

	// 商城 ID 商户所属平台商编号,非商城模式无需填写
	@XmlElement(name = "platformID", required = false)
	private String platformID = "";

	// 商户回调地址 String(256) - 用于在客户支付完成后跳转回商户指定的 URL
	@XmlElement(name = "returnUrl", required = true)
	private String returnUrl;

	// 商户通知地址 String(256) - 通知商户处理结果的异步通知地址
	@XmlElement(name = "noticeUrl", required = true)
	private String notifyUrl;

	// 扩展字段 String(256) 填写英文或中文字符串,照原样返回给商户
	@XmlElement(name = "remark", required = false)
	private String remark = "";

	@XmlTransient
	private NewpayPayModel model;

	public String getReturnUrl() {
		return returnUrl;
	}

	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}

	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	public NewpayPayModel getModel() {
		return model;
	}

	public void setModel(NewpayPayModel model) {
		this.model = model;
	}

	private String buildSignMsg() {
		// @formatter:off
		return new StringBuilder()
		 .append("version=").append(version)
		 .append("&serialID=").append(requestId)
		 .append("&submitTime=").append(formatTimestamp(submitTime))
		 .append("&failureTime=").append(formatTimestamp(failureTime))
		 .append("&customerIP=").append(customerIpAddress)
		 .append("&orderDetails=").append(orderDetails)
		 .append("&totalAmount=").append(totalAmount)
		 .append("&type=").append(type)
		 .append("&buyerMarked=").append(buyerMarked)
		 .append("&payType=").append(payType)
		 .append("&orgCode=").append(orgCode)
		 .append("&currencyCode=").append(currencyCode)
		 .append("&directFlag=").append(directFlag)
		 .append("&borrowingMarked=").append(borrowingMarked)
		 .append("&couponFlag=").append(couponFlag)
		 .append("&platformID=").append(platformID)
		 .append("&returnUrl=").append(returnUrl)
		 .append("&noticeUrl=").append(notifyUrl)
		 .append("&partnerID=").append(partnerId)
		 .append("&remark=").append(remark)
		 .append("&charset=").append(charset)
		 .append("&signType=").append(signType)
		 .toString();
		// @formatter:on
	}

	NewpayPayRequest build() {
		if (signature == null) {
			throw new NewpayException("Signature of this request is not set");
		}
		requestId = newRequestId();
		submitTime = new Date();
		totalAmount = model.getTotalAmount();
		orderDetails = model.toOrderDetails();
		String signMsg = buildSignMsg();
		sign = signRsaSignature(signMsg, NewpayCharset.fromString(charset));
		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NewpayPayRequest [submitTime=").append(submitTime).append(", failureTime=").append(failureTime)
				.append(", customerIpAddress=").append(customerIpAddress).append(", orderDetails=").append(orderDetails)
				.append(", totalAmount=").append(totalAmount).append(", type=").append(type).append(", buyerMarked=")
				.append(buyerMarked).append(", payType=").append(payType).append(", orgCode=").append(orgCode)
				.append(", currencyCode=").append(currencyCode).append(", directFlag=").append(directFlag)
				.append(", borrowingMarked=").append(borrowingMarked).append(", couponFlag=").append(couponFlag)
				.append(", platformID=").append(platformID).append(", returnUrl=").append(returnUrl)
				.append(", notifyUrl=").append(notifyUrl).append(", remark=").append(remark).append(", model=")
				.append(model).append(", version=").append(version).append(", requestId=").append(requestId)
				.append(", partnerId=").append(partnerId).append(", charset=").append(charset).append(", signType=")
				.append(signType).append(", sign=").append(sign).append("]");
		return builder.toString();
	}

	// public static Builder builder() {
	// return new Builder();
	// }

	// public static class Builder {
	// private String notifyUrl;
	// private String returnUrl;
	// private String partnerId;
	// private NewpayPayModel model;
	//
	// private Builder() {
	// }
	//
	// public Builder notifyUrl(String notifyUrl) {
	// this.notifyUrl = notifyUrl;
	// return this;
	// }
	//
	// public Builder returnUrl(String returnUrl) {
	// this.returnUrl = returnUrl;
	// return this;
	// }
	//
	// public Builder partnerId(String partnerId) {
	// this.partnerId = partnerId;
	// return this;
	// }
	//
	// public Builder model(NewpayPayModel model) {
	// this.model = model;
	// return this;
	// }
	//
	// public final NewpayPayRequest build() {
	// NewpayPayRequest req = new NewpayPayRequest();
	// req.notifyUrl = notifyUrl;
	// req.returnUrl = returnUrl;
	// req.partnerId = partnerId;
	// req.model = model;
	// return req.build();
	// }
	// }

}
