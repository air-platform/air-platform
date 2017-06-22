package net.aircommunity.platform.service.internal.payment.newpay;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * NewpayPay generic response (async notification from server)
 * 
 * @author Bin.Zhang
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class NewpayResponse extends NewpayMessage {
	private static final long serialVersionUID = 1L;

	// 商户 供的唯一订单号 String(32)
	@XmlElement(name = "orderID", required = true)
	protected String orderNo;

	// 受理结果码 (该字段只标识本次请求的 受理结果,不表示订单的实际交易结果)
	@XmlElement(name = "resultCode", required = true)
	protected String resultCode;

	// 交易状态码 (不同业务意思不同)
	@XmlElement(name = "stateCode", required = true)
	protected String stateCode;

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

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getResultMessage() {
		return getResultMessage(resultCode);
	}

	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	public String getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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

	public boolean isRequestSuccessful() {
		return RC_SUCCESS.equals(resultCode);
	}

	public boolean isTradeSuccessful() {
		return SC_SUCCESS.equals(stateCode);
	}

	public abstract void verifySignature();

}
