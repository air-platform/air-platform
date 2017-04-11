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

	@Value("${air.common.file-upload-dir}")
	private String fileUploadDir;


	@Value("${air.common.mail.from}")
	private String mailFrom;

	@Value("${air.common.sms.url}")
	private String smsUrl;

	@Value("${air.common.sms.app-key}")
	private String smsAppKey;

	@Value("${air.common.sms.app-secret}")
	private String smsAppSecret;

	@Value("${air.common.sms.extend}")
	private String smsExtend;

	@Value("${air.common.sms.type}")
	private String smsType;

	@Value("${air.common.sms.sign}")
	private String smsSign;


	@Value("${air.common.sms.tmpl-code}")
	private String smsTmplCode;

	public String getFileUploadDir() {
		return fileUploadDir;
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

	public Set<String> getAuthMethods() {
		return Stream.of(authMethods.split(AUTH_METHODS_SEP)).filter(auth -> Strings.isNotBlank(auth)).map(String::trim)
				.collect(ImmutableCollectors.toSet());
	}

}
