package net.aircommunity.platform;

import java.util.Set;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import net.aircommunity.platform.common.base.Strings;
import net.aircommunity.platform.common.collect.ImmutableCollectors;

/**
 * Global configuration used by all services and rest resources.
 * 
 * @author Bin.Zhang
 */
@Component
public class Configuration {
	private static final String AUTH_METHODS_SEP = ",";

	// @Value("${air.security.auth-methods}")
	private String authMethods;

	// for local file upload
	@Value("${air.fileupload.dir}")
	private String fileUploadDir;

	// for cloud file upload
	@Value("${air.fileload.host}")
	private String fileUploadHost;

	@Value("${air.fileload.bucket}")
	private String fileUploadBucket;

	@Value("${air.fileload.access-key}")
	private String fileUploadAccessKey;

	@Value("${air.fileload.secret-key}")
	private String fileUploadSecretKey;

	@Value("${air.mail.from}")
	private String mailFrom;

	@Value("${air.sms.url}")
	private String smsUrl;

	@Value("${air.sms.app-key}")
	private String smsAppKey;

	@Value("${air.sms.app-secret}")
	private String smsAppSecret;

	@Value("${air.sms.extend}")
	private String smsExtend;

	@Value("${air.sms.type}")
	private String smsType;

	@Value("${air.sms.sign}")
	private String smsSign;

	@Value("${air.sms.tmpl-code}")
	private String smsTmplCode;

	// NodeBB

	@Value("${air.nodebb.url}")
	private String nodebbUrl;

	@Value("${air.nodebb.token}")
	private String nodebbToken;

	public String getFileUploadDir() {
		return fileUploadDir;
	}

	public String getFileUploadHost() {
		return fileUploadHost;
	}

	public String getFileUploadBucket() {
		return fileUploadBucket;
	}

	public String getFileUploadAccessKey() {
		return fileUploadAccessKey;
	}

	public String getFileUploadSecretKey() {
		return fileUploadSecretKey;
	}

	public String getMailFrom() {
		return mailFrom;
	}

	public String getSmsUrl() {
		return smsUrl;
	}

	public String getSmsAppKey() {
		return smsAppKey;
	}

	public String getSmsAppSecret() {
		return smsAppSecret;
	}

	public String getSmsExtend() {
		return smsExtend;
	}

	public String getSmsType() {
		return smsType;
	}

	public String getSmsSign() {
		return smsSign;
	}

	public String getSmsTmplCode() {
		return smsTmplCode;
	}

	public String getNodebbUrl() {
		return nodebbUrl;
	}

	public String getNodebbToken() {
		return nodebbToken;
	}

	public Set<String> getAuthMethods() {
		return Stream.of(authMethods.split(AUTH_METHODS_SEP)).filter(auth -> Strings.isNotBlank(auth)).map(String::trim)
				.collect(ImmutableCollectors.toSet());
	}

}
