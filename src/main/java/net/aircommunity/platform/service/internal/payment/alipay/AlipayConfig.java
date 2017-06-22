package net.aircommunity.platform.service.internal.payment.alipay;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import io.micro.annotation.constraint.NotEmpty;

/**
 * Alipay Configuration
 * 
 * @author Bin.Zhang
 */
@Validated
@Configuration
@ConfigurationProperties(prefix = "air.payment.alipay")
public class AlipayConfig {

	@NotEmpty
	private String appId;

	@NotEmpty
	private String notifyUrl;

	@NotEmpty
	private String returnUrl;

	@NotEmpty
	private String appPrivateKey;

	@NotEmpty
	private String publicKey;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
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

	public String getAppPrivateKey() {
		return appPrivateKey;
	}

	public void setAppPrivateKey(String appPrivateKey) {
		this.appPrivateKey = appPrivateKey;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AlipayConfig [appId=").append(appId).append(", notifyUrl=").append(notifyUrl)
				.append(", returnUrl=").append(returnUrl).append(", appPrivateKey=").append("********")
				.append(", publicKey=").append("********").append("]");
		return builder.toString();
	}
}
