package net.aircommunity.platform;

import java.time.ZoneId;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Global configuration used by all services and rest resources.
 * 
 * @author Bin.Zhang
 */
@Component
public class Configuration {

	// REST
	private String publicBaseUrl;
	private String apiBaseUrl;
	private static ZoneId zoneId;

	@Value("${micro.rest.context-path}")
	private String contextPath;

	@Value("${micro.rest.api-version}")
	private String apiVersion;

	// AIR
	@Value("${air.time-zone}")
	private String timeZone;

	@Value("${air.name}")
	private String company;

	@Value("${air.website}")
	private String website;

	@Value("${air.use-tls}")
	private boolean useTls;

	@Value("${air.public-host}")
	private String publicHost;

	@Value("${air.public-port}")
	private int publicPort;

	@Value("${air.security.access-token-expiration-time}")
	private long tokenExpirationTimeSeconds = 60 * 60 * 24 * 7; // 7 Days

	@Value("${air.security.access-token-refresh-time}")
	private long tokenRefreshTimeSeconds = 60 * 60 * 24 * 7; // 7 Days

	@Value("${air.api.metrics-enabled}")
	private boolean apiMetricsEnabled;

	@Value("${air.account.default-avatar}")
	private String defaultAvatar;

	// for cloud file upload
	@Value("${air.fileupload.host}")
	private String fileUploadHost;

	@Value("${air.fileupload.bucket}")
	private String fileUploadBucket;

	@Value("${air.fileupload.access-key}")
	private String fileUploadAccessKey;

	@Value("${air.fileupload.secret-key}")
	private String fileUploadSecretKey;

	@Value("${air.order.email-notification}")
	private boolean orderEmailNotificationEnabled;

	@Value("${air.order.email-notification-subject}")
	private String orderEmailNotificationSubject;

	// cron job
	@Value("${air.order.payment-sync-schedule}")
	private String orderPaymentSyncSchedule;

	@Value("${air.order.query-day-max}")
	private int orderQueryDayMax;

	@Value("${air.order.search-result-max}")
	private int orderSearchResultMax;

	@Value("${air.order.rebuild-ref}")
	private boolean orderRebuildRef;

	// Redirect to a page after returned from payment gateway
	@Value("${air.payment.gateway-redirect}")
	private String paymentGatewayRedirect;

	@Value("${air.mobile.verification}")
	private boolean mobileVerificationEnabled;

	@Value("${air.mail.from}")
	private String mailFrom;

	@Value("${air.mail.queue-size}")
	private int mailQueueSize;

	@Value("${air.mail.verification-subject}")
	private String mailVerificationSubject;

	@Value("${air.mail.resetpassword-subject}")
	private String mailResetPasswordSubject;

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
	@Value("${air.airq.url}")
	private String airqUrl;

	@Value("${air.airq.token}")
	private String airqToken;

	@Value("${air.airq.account-sync}")
	private boolean airqAccountSync;

	// IDCard service
	@Value("${air.idcard.url}")
	private String idcardUrl;

	@Value("${air.idcard.token}")
	private String idcardToken;

	@Value("${air.ueditor.image-url}")
	private String ueditorImageUrl;

	@Value("${air.ueditor.image-action-name}")
	private String ueditorImageActionName;

	@Value("${air.ueditor.image-url-prefix}")
	private String ueditorImageUrlPrefix;

	@Value("${air.ueditor.image-field-name}")
	private String ueditorImageFieldName;

	@Value("${air.ueditor.image-max-size}")
	private int ueditorImageMaxSize;

	@Value("${air.ueditor.image-allow-files}")
	private String ueditorImageAllowFiles;

	@Value("${air.ueditor.image-compress-enable}")
	private boolean ueditorImageCompressEnable;

	@Value("${air.ueditor.image-compress-border}")
	private int ueditorImageCompressBorder;

	@Value("${air.ueditor.image-insert-align}")
	private String ueditorImageInsertAlign;

	@Value("${air.ueditor.image-path-format}")
	private String ueditorImagePathFormat;


	@Value("${air.ueditor.scrawl-action-name}")
	private String ueditorScrawlActionName;

	@Value("${air.ueditor.scrawl-field-name}")
	private String ueditorScrawlFieldName;

	@Value("${air.ueditor.scrawl-path-format}")
	private String ueditorScrawlPathFormat;

	@Value("${air.ueditor.scrawl-max-size}")
	private int ueditorScrawlMaxSize;

	@Value("${air.ueditor.scrawl-url-prefix}")
	private String ueditorScrawlUrlPrefix;

	@Value("${air.ueditor.scrawl-insert-align}")
	private String ueditorScrawlInsertAlign;

	@Value("${air.notification.master-secret}")
	private String notificationMasterSecret;

	@Value("${air.notification.app-key}")
	private String notificationAppKey;


	@PostConstruct
	private void init() {
		boolean useTls = isUseTls();
		String host = getPublicHost();
		int port = getPublicPort();
		StringBuilder builder = new StringBuilder().append(useTls ? "https://" : "http://").append(host);
		if (port != 80 && port != 443) {
			builder.append(":").append(port);
		}
		publicBaseUrl = builder.toString();
		// API URL
		String apiVersion = getApiVersion();
		String contextPath = getContextPath();
		builder.setLength(0);
		builder.append(getPublicBaseUrl());
		if (!contextPath.startsWith("/")) {
			contextPath = "/" + contextPath;
		}
		if (!contextPath.endsWith("/")) {
			contextPath += "/";
		}
		// it should be now: contextPath=/path/
		apiBaseUrl = builder.append(contextPath).append(apiVersion).toString();
		//
		zoneId = ZoneId.of(getTimeZone());
		paymentGatewayRedirect = new StringBuilder(publicBaseUrl).append("/").append(paymentGatewayRedirect).toString();
	}

	/**
	 * Make it static allow to call it from anywhere.
	 * 
	 * @return configured ZoneId of current platform
	 */
	public static ZoneId getZoneId() {
		return zoneId;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public String getCompany() {
		return company;
	}

	public String getWebsite() {
		return website;
	}

	public long getTokenExpirationTimeSeconds() {
		return tokenExpirationTimeSeconds;
	}

	public long getTokenRefreshTimeSeconds() {
		return tokenRefreshTimeSeconds;
	}

	public boolean isApiMetricsEnabled() {
		return apiMetricsEnabled;
	}

	public boolean isUseTls() {
		return useTls;
	}

	public String getPublicHost() {
		return publicHost;
	}

	public int getPublicPort() {
		return publicPort;
	}

	public String getContextPath() {
		return contextPath;
	}

	public String getApiVersion() {
		return apiVersion;
	}

	public String getPublicBaseUrl() {
		return publicBaseUrl;
	}

	public String getApiBaseUrl() {
		return apiBaseUrl;
	}

	public String getDefaultAvatar() {
		return defaultAvatar;
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

	public boolean isOrderEmailNotificationEnabled() {
		return orderEmailNotificationEnabled;
	}

	public String getOrderEmailNotificationSubject() {
		return orderEmailNotificationSubject;
	}

	public String getPaymentGatewayRedirect() {
		return paymentGatewayRedirect;
	}

	public String getOrderPaymentSyncSchedule() {
		return orderPaymentSyncSchedule;
	}

	public int getOrderQueryDayMax() {
		return orderQueryDayMax;
	}

	public int getOrderSearchResultMax() {
		return orderSearchResultMax;
	}

	public boolean isOrderRebuildRef() {
		return orderRebuildRef;
	}

	public boolean isMobileVerificationEnabled() {
		return mobileVerificationEnabled;
	}

	public String getMailFrom() {
		return mailFrom;
	}

	public int getMailQueueSize() {
		return mailQueueSize;
	}

	public String getMailVerificationSubject() {
		return mailVerificationSubject;
	}

	public String getMailResetPasswordSubject() {
		return mailResetPasswordSubject;
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

	public String getAirqUrl() {
		return airqUrl;
	}

	public String getAirqToken() {
		return airqToken;
	}

	public boolean isAirqAccountSync() {
		return airqAccountSync;
	}

	public String getIdcardUrl() {
		return idcardUrl;
	}

	public String getIdcardToken() {
		return idcardToken;
	}

	public String getUeditorImageUrl() {
		return ueditorImageUrl;
	}

	public String getUeditorImageActionName() {
		return ueditorImageActionName;
	}

	public String getUeditorImageUrlPrefix() {
		return ueditorImageUrlPrefix;
	}

	public String getUeditorImageFieldName() {
		return ueditorImageFieldName;
	}

	public int getUeditorImageMaxSize() {
		return ueditorImageMaxSize;
	}

	public String getUeditorImageAllowFiles() {
		return ueditorImageAllowFiles;
	}

	public boolean isUeditorImageCompressEnable() {
		return ueditorImageCompressEnable;
	}

	public int getUeditorImageCompressBorder() {
		return ueditorImageCompressBorder;
	}

	public String getUeditorImageInsertAlign() {
		return ueditorImageInsertAlign;
	}

	public String getUeditorImagePathFormat() {
		return ueditorImagePathFormat;
	}

	public String getUeditorScrawlActionName() {
		return ueditorScrawlActionName;
	}

	public String getUeditorScrawlFieldName() {
		return ueditorScrawlFieldName;
	}

	public String getUeditorScrawlPathFormat() {
		return ueditorScrawlPathFormat;
	}

	public int getUeditorScrawlMaxSize() {
		return ueditorScrawlMaxSize;
	}

	public String getUeditorScrawlUrlPrefix() {
		return ueditorScrawlUrlPrefix;
	}

	public String getUeditorScrawlInsertAlign() {
		return ueditorScrawlInsertAlign;
	}

	public String getNotificationMasterSecret() {
		return notificationMasterSecret;
	}

	public String getNotificationAppKey() {
		return notificationAppKey;
	}

	// private static final String AUTH_METHODS_SEP = ",";
	// @Value("${air.security.auth-methods}")
	// private String authMethods;
	// public Set<String> getAuthMethods() {
	// return Stream.of(authMethods.split(AUTH_METHODS_SEP)).filter(auth -> Strings.isNotBlank(auth)).map(String::trim)
	// .collect(ImmutableCollectors.toSet());
	// }
}
