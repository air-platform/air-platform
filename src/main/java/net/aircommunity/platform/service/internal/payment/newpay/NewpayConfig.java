package net.aircommunity.platform.service.internal.payment.newpay;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import io.micro.annotation.constraint.NotEmpty;

/**
 * Newpay Configuration
 * 
 * @author Bin.Zhang
 */
@Validated
@Configuration
@ConfigurationProperties(prefix = "air.payment.newpay")
// @PropertySource("classpath:config/payment-dev.properties")
public class NewpayConfig {

	@NotEmpty
	private String partnerId;

	// hnapay.partner.kayPath
	@NotEmpty
	private String keystorePath;

	// hnapay.partner.storepass
	@NotEmpty
	private String keystorePassword;

	// hnapay.partner.alias (certificateEntry Alias)
	@NotEmpty
	private String certAlias;

	// hnapay.partner.pwd (certificateEntry Password)
	@NotEmpty
	private String certPassword;

	// hnapay.gateway.pubkey
	@NotEmpty
	private String gatewayPublicKey;
	// hnapay.partner.pubkey (if MD5, never use it)
	// private String partnerPublicKey;

	private String notifyUrl;

	private String returnUrl;

	public String getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
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

	public String getKeystorePath() {
		return keystorePath;
	}

	public void setKeystorePath(String keystorePath) {
		this.keystorePath = keystorePath;
	}

	public String getKeystorePassword() {
		return keystorePassword;
	}

	public void setKeystorePassword(String keystorePassword) {
		this.keystorePassword = keystorePassword;
	}

	public String getCertAlias() {
		return certAlias;
	}

	public void setCertAlias(String certAlias) {
		this.certAlias = certAlias;
	}

	public String getCertPassword() {
		return certPassword;
	}

	public void setCertPassword(String certPassword) {
		this.certPassword = certPassword;
	}

	public String getGatewayPublicKey() {
		return gatewayPublicKey;
	}

	public void setGatewayPublicKey(String gatewayPublicKey) {
		this.gatewayPublicKey = gatewayPublicKey;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NewpayConfig [partnerId=").append(partnerId).append(", notifyUrl=").append(notifyUrl)
				.append(", returnUrl=").append(returnUrl).append(", keystorePath=").append(keystorePath)
				.append(", keystorePassword=******").append(", certAlias=").append(certAlias)
				.append(", certPassword=******").append(", gatewayPublicKey=******").append("]");
		return builder.toString();
	}

}
