package net.aircommunity.platform.service.internal.payment.newpay;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.function.BiFunction;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import io.micro.common.DateFormats;
import io.micro.common.UUIDs;

/**
 * TradePayModel
 * 
 * @author Bin.Zhang
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class NewpayPayRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private final static String SUBMIT_TIME_PATTERN = "yyyyMMddHHmmss";
	private static final SimpleDateFormat SUBMIT_TIME_FORMATTER = DateFormats.simple(SUBMIT_TIME_PATTERN);

	// 版本 String(4) - 当前版本 2.6
	@XmlElement(name = "version", required = true)
	private String version = "2.6";

	// 请求序列号 String(32) - 商户本次提交请求的序列号,每次提交必须唯一 (用于验证商户重复提交)
	@XmlElement(name = "serialID", required = true)
	private String requestId;

	// 订单提交时间 String(14), yyyyMMddHHmmss
	@XmlElement(name = "submitTime", required = true)
	private String submitTime;

	// 订单失效时间 String(14), yyyyMMddHHmmss
	@XmlElement(name = "failureTime", required = false)
	private String failureTime = "";

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

	// 商户 ID String(32) - 新生支付平台 供给商户的 ID 号 (11000002981)
	@XmlElement(name = "partnerID", required = true)
	private String partnerId;

	// 扩展字段 String(256) 填写英文或中文字符串,照原样返回给商户
	@XmlElement(name = "remark", required = false)
	private String remark = "";

	// 编码方式 String(1) - 1:UTF-8
	@XmlElement(name = "charset", required = true)
	private String charset = "1";

	// 签名类型 String(1) - 1:RSA 2:MD5
	@XmlElement(name = "signType", required = true)
	private String signType = "1";

	// 签名字符串 String(256)
	@XmlElement(name = "signMsg", required = true)
	private String sign;

	@XmlTransient
	private NewpayPayModel model;

	public NewpayPayModel getModel() {
		return model;
	}

	public String getReturnUrl() {
		return returnUrl;
	}

	public String getNotifyUrl() {
		return notifyUrl;
	}

	public String getPartnerId() {
		return partnerId;
	}

	private String buildSignMsg() {
		// @formatter:off
		return new StringBuilder()
		 .append("version=").append(version)
		 .append("&serialID=").append(requestId)
		 .append("&submitTime=").append(submitTime)
		 .append("&failureTime=").append(failureTime)
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

	private String build() {
		requestId = UUIDs.shortTimebased();
		submitTime = SUBMIT_TIME_FORMATTER.format(new Date());
		// TODO REMOVE (TESTING)
		// requestId = "dde03550527111e7d2395f079d75b256";
		// submitTime = "20170616165746";
		totalAmount = model.getTotalAmount();
		orderDetails = model.toOrderDetails();
		return buildSignMsg();
	}

	public static Builder builder(BiFunction<String, NewpayCharset, String> signer) {
		return new Builder(signer);
	}

	public static class Builder {
		private final BiFunction<String, NewpayCharset, String> signer;
		private String notifyUrl;
		private String returnUrl;
		private String partnerId;
		private NewpayPayModel model;

		public Builder(BiFunction<String, NewpayCharset, String> signer) {
			this.signer = Objects.requireNonNull(signer, "signer cannot be null");
		}

		public Builder notifyUrl(String notifyUrl) {
			this.notifyUrl = notifyUrl;
			return this;
		}

		public Builder returnUrl(String returnUrl) {
			this.returnUrl = returnUrl;
			return this;
		}

		public Builder partnerId(String partnerId) {
			this.partnerId = partnerId;
			return this;
		}

		public Builder model(NewpayPayModel model) {
			this.model = model;
			return this;
		}

		public final NewpayPayRequest build() {
			NewpayPayRequest req = new NewpayPayRequest();
			req.notifyUrl = notifyUrl;
			req.returnUrl = returnUrl;
			req.partnerId = partnerId;
			req.model = model;
			String signMsg = req.build();
			try {
				req.sign = signer.apply(signMsg, NewpayCharset.fromString(req.charset));
			}
			catch (Exception e) {
				throw new IllegalArgumentException("Failed to sign request:" + e.getMessage(), e);
			}
			return req;
		}
	}

}
