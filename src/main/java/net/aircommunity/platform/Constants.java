package net.aircommunity.platform;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * Constants for the whole AIR platform (internal use only).
 * 
 * @author Bin.Zhang
 */
@SuppressWarnings("javadoc")
public final class Constants {

	// **************
	// Commons
	// **************
	public static final String CLAIM_API_KEY = "claim.apikey";
	public static final String CLAIM_ID = "id";
	public static final String CLAIM_USERNAME = "username";
	public static final String CLAIM_NICKNAME = "nickName";
	public static final String DEFAULT_ADMIN_USERNAME = "admin";
	public static final String DEFAULT_ADMIN_PASSWORD = "p0o9i8u7";
	public static final String DEFAULT_ADMIN_EMAIL = "ac_eb@hnair.com";
	public static final String DEFAULT_PASSWORD = "Air@pwd123";
	public static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";
	public static final String LOOPBACK_LOCALHOST = "127.0.0.1";
	public static final String IP_UNKNOWN = "0.0.0.0";
	public static final Set<String> LOOPBACK_ADDRESSES = ImmutableSet.of("127.0.0.1", "0:0:0:0:0:0:0:1");

	// **************
	// User Points
	// **************
	public static class PointRules {
		public static final String ACCOUNT_REGISTRATION = "account_registration";
		public static final String ORDER_FINISHED = "order_finished_percent";
		public static final String FIRST_ORDER_PRICE_OFF = "first_order_price_off";
		public static final String DAILY_SIGNIN_PREFIX = "daily_signin_";
		public static final String DAILY_SIGNIN_1 = DAILY_SIGNIN_PREFIX + "1";
		public static final String DAILY_SIGNIN_3 = DAILY_SIGNIN_PREFIX + "3";
		public static final String DAILY_SIGNIN_5 = DAILY_SIGNIN_PREFIX + "5";
		public static final String DAILY_SIGNIN_7 = DAILY_SIGNIN_PREFIX + "7";
	}

	// **************
	// API metrics
	// **************
	public static final String COUNTER_API_REQUESTS = "api.requests";

	// **************
	// Product
	// **************
	// client managers of a product
	// MUST IN FORMAT OF: person1:email1, person2:email2, ..., personN:emailN
	public static final String CONTACT_INFO_SEPARATOR = ":";
	public static final String CONTACT_SEPARATOR = ",";
	public static final String PRICE_SEPARATOR = ",";
	// randomString.extension, e.g. jki45hkfh945k3j5.jpg
	public static final String FILE_UPLOAD_NAME_FORMAT = "%s.%s";

	// **************
	// Templates
	// **************
	// @formatter:off
	private static final String TEMPLATE_DIR = "template";
	public static final String TEMPLATE_MAIL_VERIFICATION = TEMPLATE_DIR + "/mail-verification.template";
	public static final String TEMPLATE_MAIL_VERIFICATION_SUCCESS = TEMPLATE_DIR + "/mail-verification-success.template";
	public static final String TEMPLATE_MAIL_VERIFICATION_FAILURE = TEMPLATE_DIR + "/mail-verification-failure.template";
	public static final String TEMPLATE_MAIL_RESET_PASSOWRD = TEMPLATE_DIR + "/mail-reset-password.template";
	public static final String TEMPLATE_MAIL_ORDER_NOTIFICATION = TEMPLATE_DIR + "/mail-%s-order.template";
	public static final String TEMPLATE_SMS_ORDER_EVENT_NOTIFICATION = TEMPLATE_DIR + "/sms-%s-event.template";
	// @formatter:on
	// template binding keys
	public static final String TEMPLATE_BINDING_USERNAME = "username";
	public static final String TEMPLATE_BINDING_COMPANY = "company";
	public static final String TEMPLATE_BINDING_WEBSITE = "website";
	public static final String TEMPLATE_BINDING_EMAIL = "email";
	public static final String TEMPLATE_BINDING_FAILURE_CAUSE = "cause";
	public static final String TEMPLATE_BINDING_VERIFICATIONLINK = "verificationLink";
	public static final String TEMPLATE_BINDING_RNDPASSWORD = "rndPassword";

	// any constants put here

	private Constants() {
		throw new AssertionError();
	}
}
