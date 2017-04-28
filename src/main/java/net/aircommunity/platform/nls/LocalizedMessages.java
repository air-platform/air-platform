package net.aircommunity.platform.nls;

import java.util.Locale;

import io.micro.common.NLSResource;

/**
 * NLS Messages.
 * 
 * @author Bin.Zhang
 */
public class LocalizedMessages extends NLSResource {
	// NLS resource bundle
	private static final String RESOURCE_BUNDLE_BASENAME = LocalizedMessages.class.getPackage().getName() + ".message";

	public static String PRODUCT_NOT_FOUND;

	public static String ORDER_OPERATION_SUBMIT;
	public static String ORDER_OPERATION_CANCEL;
	public static String ORDER_NOT_FOUND;
	public static String ORDER_ILLEGAL_STATUS;

	public static String COMMENT_NOT_ALLOWED;
	public static String COMMENT_NOT_FOUND;
	public static String COMMENT_NOT_ALLOWED_ORDER_NOT_FINISHED;

	public static String SCHOOL_NOT_FOUND;
	public static String COURSE_NOT_FOUND;

	public static String FLEET_NOT_FOUND;
	public static String FLEET_ALREADY_EXISTS;
	public static String FERRYFLIGHT_ALREADY_EXISTS;

	public static String AIRPORT_ICAO_ALREADY_EXISTS;
	public static String AIRPORT_IATA_ALREADY_EXISTS;
	public static String AIRPORT_INVALID_CODE;
	public static String AIRPORT_NOT_FOUND;

	public static String AIRJET_ALREADY_EXISTS;
	public static String AIRJET_TYPE_NOT_FOUND;
	public static String AIRJET_NOT_FOUND;
	public static String AIRCRAFT_ALREADY_EXISTS;
	public static String AIRCRAFT_ITEM_NOT_FOUND;

	public static String ACCOUNT_EMAIL_NOT_BIND;
	public static String ACCOUNT_MOBILE_NOT_FOUND;
	public static String ACCOUNT_PASSWORD_MISMATCH;
	public static String ACCOUNT_UNAUTHORIZED_PERMISSION;
	public static String ACCOUNT_CREATION_FAILURE;
	public static String ACCOUNT_CREATION_ADMIN_NOT_ALLOWED;
	public static String ACCOUNT_CREATION_INVALID_VERIFICATION_CODE;
	public static String ACCOUNT_ADD_PASSENGER_FAILURE;
	public static String ACCOUNT_PASSENGER_NOT_ALLOWED;
	public static String ACCOUNT_ADDRESS_NOT_ALLOWED;
	public static String ACCOUNT_EMAIL_ALREADY_EXISTS;
	public static String ACCOUNT_USERNAME_ALREADY_EXISTS;
	public static String ACCOUNT_ALREADY_EXISTS;
	public static String ACCOUNT_NOT_FOUND;
	public static String ACCOUNT_INVALID_VERIFICATION_CODE;
	public static String ACCOUNT_EMAIL_ALREADY_VERIFIED;
	public static String PASSENGER_NOT_FOUND;
	public static String PASSENGER_INFO_REQUIRED;

	public static String SMS_TOO_MANY_VERIFICATION_REQUEST;
	public static String SMS_SEND_FAILURE;

	public static String AIRQ_ERROR;

	public static String SERVICE_UNAVAILABLE;

	// internal server error
	public static String INTERNAL_SERVER_ERROR;

	/**
	 * Bind the messageId with bindings.
	 * 
	 * @param messageId the messageId
	 * @param bindings the bindings
	 * @return localized message
	 */
	public static String msg(String messageId, Object... bindings) {
		return bind(RESOURCE_BUNDLE_BASENAME, Locale.CHINESE, messageId, bindings);
	}

	/**
	 * Bind the messageId with bindings.
	 * 
	 * @param locale the locale
	 * @param messageId the messageId
	 * @param bindings the bindings
	 * @return localized message
	 */
	public static String msg(Locale locale, String messageId, Object... bindings) {
		return bind(RESOURCE_BUNDLE_BASENAME, locale, messageId, bindings);
	}

	static {
		// initialize message binding from the message.properties file
		initialize(LocalizedMessages.class);
	}
}
