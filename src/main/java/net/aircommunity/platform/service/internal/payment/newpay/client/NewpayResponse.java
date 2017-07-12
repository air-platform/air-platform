package net.aircommunity.platform.service.internal.payment.newpay.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Newpay generic response (async notification from server) (XXX for payment only?)
 * 
 * @author Bin.Zhang
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class NewpayResponse extends NewpayMessage {
	private static final long serialVersionUID = 1L;

	// 受理结果码 (该字段只标识本次请求的 受理结果,不表示订单的实际交易结果)
	@XmlElement(name = "resultCode", required = true)
	protected String resultCode;

	// 商户 ID String(32) - 新生支付平台 供给商户的 ID 号 (11000002981)
	@XmlElement(name = "partnerID", required = true)
	protected String partnerId;

	// 扩展字段 String(256) 填写英文或中文字符串,照原样返回给商户
	@XmlElement(name = "remark", required = false)
	protected String remark = "";

	// 编码方式 String(1) - 1:UTF-8
	@XmlElement(name = "charset", required = true)
	protected String charset = "1";

	// 签名类型 String(1) - 1:RSA 2:MD5
	@XmlElement(name = "signType", required = true)
	protected String signType = "1";

	// 签名字符串 String(256)
	@XmlElement(name = "signMsg", required = true)
	protected String sign;

	public String getResultCode() {
		return resultCode;
	}

	public String getResultMessage() {
		return getResultMessage(resultCode);
	}

	public String getPartnerId() {
		return partnerId;
	}

	public String getRemark() {
		return remark;
	}

	public String getCharset() {
		return charset;
	}

	public String getSignType() {
		return signType;
	}

	public String getSign() {
		return sign;
	}

	public boolean isRequestSuccessful() {
		return RC_SUCCESS.equals(resultCode);
	}

	public abstract void verifySignature();

}
