package net.aircommunity.platform.service.internal.payment.newpay;

import javax.xml.bind.annotation.XmlElement;

/**
 * Newpay generic request.
 * 
 * @author Bin.Zhang
 */
public class NewpayRequest extends NewpayMessage {
	private static final long serialVersionUID = 1L;

	// 版本 String(4) - 当前版本 2.6
	@XmlElement(name = "version", required = true)
	protected String version = VERSION;

	// 请求序列号 String(32) - 商户本次提交请求的序列号,每次提交必须唯一 (用于验证商户重复提交)
	@XmlElement(name = "serialID", required = true)
	protected String requestId;

	// 商户 ID String(32) - 新生支付平台 供给商户的 ID 号 (11000002981)
	@XmlElement(name = "partnerID", required = true)
	protected String partnerId;

	// 编码方式 String(1) - 1:UTF-8
	@XmlElement(name = "charset", required = true)
	protected String charset = "1";

	// 签名类型 String(1) - 1:RSA 2:MD5
	@XmlElement(name = "signType", required = true)
	protected String signType = "1";

	// 签名字符串 String(256)
	@XmlElement(name = "signMsg", required = true)
	protected String sign;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getSignType() {
		return signType;
	}

	public void setSignType(String signType) {
		this.signType = signType;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

}
