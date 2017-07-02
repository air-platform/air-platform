package net.aircommunity.platform.service.internal.payment.wechat;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import io.micro.annotation.constraint.NotEmpty;

/**
 * Wechat Configuration
 * 
 * @author Bin.Zhang
 */
@Validated
@Configuration
@ConfigurationProperties(prefix = "air.payment.wechat")
public class WechatConfig {

	// 微信公众账号或开放平台APP的唯一标识
	@NotEmpty
	private String appId;

	// 微信支付商户号
	@NotEmpty
	private String mchId;

	// API密钥(微信支付商户密钥)
	@NotEmpty
	private String mchKey;

	private String notifyUrl;

	private String returnUrl;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getMchId() {
		return mchId;
	}

	public void setMchId(String mchId) {
		this.mchId = mchId;
	}

	public String getMchKey() {
		return mchKey;
	}

	public void setMchKey(String mchKey) {
		this.mchKey = mchKey;
	}

	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	public String getReturnUrl() {
		return returnUrl;
	}

	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("WechatConfig [appId=").append(appId).append(", mchId=").append(mchId).append(", mchKey=")
				.append("******").append(", notifyUrl=").append(notifyUrl).append(", returnUrl=").append(returnUrl)
				.append("]");
		return builder.toString();
	}

}
