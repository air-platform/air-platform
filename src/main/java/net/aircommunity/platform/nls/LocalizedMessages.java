package net.aircommunity.platform.nls;

import java.util.Locale;

import io.micro.common.nls.NLSResource;

/**
 * NLS Messages.
 * 
 * @author Bin.Zhang
 */
public class LocalizedMessages extends NLSResource {
	// NLS resource bundle
	private static final String RESOURCE_BUNDLE_BASENAME = LocalizedMessages.class.getPackage().getName() + ".message";

	// message for display
	public static String USERNAME_ANONYMOUS;
	public static String FLEET_OFFERED;
	public static String FLEET_NOT_SELECTED;
	public static String CUSTOMER_FEEDBACK;
	public static String CUSTOMER_COOPERATION_INTENTION;

	// payment
	public static String PAYMENT_SERVICE_UNAVAILABLE;
	public static String PAYMENT_SUCCESS_SERVER_NOTIFY;
	public static String PAYMENT_SUCCESS_SERVER_QUERY;
	public static String PAYMENT_PRODUCT_DESCRIPTION;
	public static String PAYMENT_SERVER_NOTIFY_BIZ_FAILURE;
	public static String PAYMENT_SERVER_NOTIFY_TRADE_FAILURE;
	public static String PAYMENT_REQUESTED;
	public static String PAYMENT_AUTO_REFUND_ON_FAILURE;

	// refund
	public static String REFUND_SUCCESS;
	public static String REFUND_FAILURE;
	public static String REFUND_BIZ_FAILURE;
	public static String REFUND_REQUEST_REJECTED;
	public static String REFUND_REASON_MISSING;
	public static String REFUND_REASON_FORCE;
	public static String REFUND_REQUESTED;
	public static String REFUND_SUCCESS_SERVER_NOTIFY;
	public static String REFUND_SUCCESS_SERVER_QUERY;
	public static String REFUND_SERVER_NOTIFY_TRADE_FAILURE;
	public static String REFUND_ON_PAYMENT_NOTIFY_FAILURE; // auto refund message

	// generic
	public static String FORM_INVALID_FILE_INPUT;

	// product
	public static String PRODUCT_NOT_SET;
	public static String PRODUCT_NOT_FOUND;
	public static String PRODUCT_NOT_APPROVED;
	public static String PRODUCT_NOT_PUBLISHED;
	public static String PRODUCT_ACCESS_DENIED;
	public static String PRODUCT_FAQ_NOT_FOUND;
	public static String PRODUCT_FAMILY_NOT_FOUND;
	public static String PRODUCT_FAMILY_NOT_APPROVED;
	public static String PRODUCT_INVALID_DEPARTURE_DATE;
	public static String PRODUCT_CANNOT_BE_DELETED;
	public static String PRODUCT_UPDATED_FAILED;
	public static String PRODUCT_FAMILY_CANNOT_BE_DELETED;
	public static String TENANT_PRODUCT_FAMILIES_CANNOT_BE_DELETED;
	public static String TENANT_PRODUCTS_CANNOT_BE_DELETED;
	public static String SCHOOL_COURSES_CANNOT_BE_DELETED;
	public static String SCHOOL_CANNOT_BE_DELETED;
	public static String TENANT_SCHOOLS_CANNOT_BE_DELETED;
	public static String AIRCRAFT_CANNOT_BE_DELETED;
	public static String AIRCRAFTS_CANNOT_BE_DELETED;

	// banner & promotion & airclass
	public static String BANNER_NOT_FOUND;
	public static String BANNER_INVALID_LINK_CATEGORY;
	public static String PROMOTION_NOT_FOUND;
	public static String AIRCLASS_NOT_FOUND;

	// order
	// operation used in email notfication
	public static String ORDER_OPERATION_SUBMIT;
	public static String ORDER_OPERATION_CANCEL;
	public static String ORDER_OPERATION_PAY;
	public static String ORDER_OPERATION_REFUND_REQUESTED;
	//
	public static String ORDER_NOT_FOUND;
	public static String ORDER_TOTAL_AMOUNT_MISMATCH;
	public static String ORDER_INSTALMENT_NOT_FOUND;
	public static String ORDER_ACCESS_DENIED;
	public static String ORDER_ILLEGAL_STATUS;
	public static String ORDER_PAYMENT_NOT_FOUND;
	public static String ORDER_NOT_PAYABLE;
	public static String ORDER_ALREADY_PAID;
	public static String ORDER_REFUND_FAILURE;
	public static String ORDER_INVALID_OFFER_TOTAL_AMOUNT;
	public static String ORDER_CANNOT_BE_DELETED;
	public static String ORDER_INVALID_PRICING_ARGS;
	public static String USER_ORDERS_CANNOT_BE_DELETED;

	public static String AIRPORT_ICAO_ALREADY_EXISTS;
	public static String AIRPORT_IATA_ALREADY_EXISTS;
	public static String AIRPORT_INVALID_CODE;
	public static String AIRPORT_NOT_FOUND;
	public static String AIRJET_ALREADY_EXISTS;
	public static String AIRJET_TYPE_NOT_FOUND;
	public static String AIRJET_NOT_FOUND;
	public static String APRON_NOT_FOUND;
	public static String AIRCRAFT_ALREADY_EXISTS;
	public static String AIRCRAFT_NOT_FOUND;
	public static String SALESPACKAGE_NOT_FOUND;
	public static String CITYSITE_NOT_FOUND;

	// comment
	public static String COMMENT_NOT_ALLOWED;
	public static String COMMENT_NOT_FOUND;
	public static String COMMENT_INVALID_SOURCE;
	public static String COMMENT_NOT_ALLOWED_ORDER_NOT_FINISHED;

	// specific products
	public static String SCHOOL_NOT_FOUND;
	public static String COURSE_NOT_FOUND;
	public static String FLEET_NOT_FOUND;
	public static String FLEET_ALREADY_EXISTS;
	public static String ORDER_ITEM_CANDIDATE_NOT_FOUND; // generic
	public static String FLEET_CANDIDATE_NOT_FOUND;
	public static String AIRCRAFT_CANDIDATE_NOT_FOUND;

	// account
	public static String ACCOUNT_ADMIN_CANNOT_BE_DELETED;
	public static String ACCOUNT_TYPE_MISMATCH;
	public static String ACCOUNT_EMAIL_NOT_BIND;
	public static String ACCOUNT_MOBILE_NOT_FOUND;
	public static String ACCOUNT_PASSWORD_MISMATCH;
	public static String ACCOUNT_UNAUTHORIZED;
	public static String ACCOUNT_UNAUTHORIZED_DISABLED;
	public static String ACCOUNT_PERMISSION_DENIED_LOCKED;
	public static String ACCOUNT_PERMISSION_DENIED;
	public static String ACCOUNT_NOT_TENANT;
	public static String ACCOUNT_UPDATE_ADMIN_STATUS_NOT_ALLOWED;
	public static String ACCOUNT_UPDATE_ROLE_NOT_ALLOWED;
	public static String ACCOUNT_INVALID_ROLE;
	public static String ACCOUNT_CREATION_FAILURE;
	public static String ACCOUNT_CREATION_ADMIN_NOT_ALLOWED;
	public static String ACCOUNT_CREATION_INVALID_VERIFICATION_CODE;
	public static String ACCOUNT_RESET_PASSWORD_INVALID_VERIFICATION_CODE;
	public static String ACCOUNT_ADD_PASSENGER_FAILURE;
	public static String ACCOUNT_INVALID_IDCARD;
	public static String ACCOUNT_PASSENGER_NOT_ALLOWED;
	public static String ACCOUNT_ADDRESS_NOT_ALLOWED;
	public static String ACCOUNT_EMAIL_ALREADY_EXISTS;
	public static String ACCOUNT_USERNAME_ALREADY_EXISTS;
	public static String ACCOUNT_ALREADY_EXISTS;
	public static String ACCOUNT_NOT_FOUND;
	public static String ACCOUNT_INVALID_VERIFICATION_CODE;
	public static String ACCOUNT_EMAIL_ALREADY_VERIFIED;
	public static String PASSENGER_NOT_FOUND;
	public static String PASSENGER_ALREADY_EXISTS;
	public static String PASSENGER_INFO_REQUIRED;
	public static String IDCARD_VERIFICATION_FAILURE;
	public static String SMS_TOO_MANY_VERIFICATION_REQUEST;
	public static String SMS_SEND_FAILURE;

	public static String AIRQ_ERROR;

	public static String SERVICE_UNAVAILABLE;
	// internal server error
	public static String INTERNAL_SERVER_ERROR;

	//push notification
	public static String PUSHNOTIFICATION_NOT_FOUND;
	public static String PUSHNOTIFICATION_CANNOT_BE_DELETED;

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
