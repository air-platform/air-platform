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

	// templates
	// @formatter:off
	private static final String TEMPLATE_DIR = "template";
	public static final String TEMPLATE_MAIL_VERIFICATION = TEMPLATE_DIR + "/mail-verification.template";
	public static final String TEMPLATE_MAIL_VERIFICATION_SUCCESS = TEMPLATE_DIR + "/mail-verification-success.template";
	public static final String TEMPLATE_MAIL_VERIFICATION_FAILURE = TEMPLATE_DIR + "/mail-verification-failure.template";
	public static final String TEMPLATE_MAIL_RESET_PASSOWRD = TEMPLATE_DIR + "/mail-reset-password.template";
	public static final String TEMPLATE_MAIL_ORDER_NOTIFICATION = TEMPLATE_DIR + "/mail-%s-order.template";
	// @formatter:on

	// bindings
	public static final String TEMPLATE_BINDING_USERNAME = "username";
	public static final String TEMPLATE_BINDING_COMPANY = "company";
	public static final String TEMPLATE_BINDING_WEBSITE = "website";
	public static final String TEMPLATE_BINDING_EMAIL = "email";
	public static final String TEMPLATE_BINDING_FAILURE_CAUSE = "cause";
	public static final String TEMPLATE_BINDING_VERIFICATIONLINK = "verificationLink";
	public static final String TEMPLATE_BINDING_RNDPASSWORD = "rndPassword";

	// metrics
	public static final String COUNTER_API_REQUESTS = "api.requests";

	// client managers of a product
	// MUST IN FORMAT OF: person1:email1, person2:email2, ..., personN:emailN
	public static final String CONTACT_INFO_SEPARATOR = ":";
	public static final String CONTACT_SEPARATOR = ",";

	//
	public static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";
	public static final String LOOPBACK_LOCALHOST = "127.0.0.1";
	public static final Set<String> LOOPBACK_ADDRESSES = ImmutableSet.of("127.0.0.1", "0:0:0:0:0:0:0:1");

	//
	public static final String CLAIM_API_KEY = "claim.apikey";
	public static final String CLAIM_USERNAME = "username";
	public static final String DEFAULT_ADMIN_USERNAME = "admin";
	public static final String DEFAULT_ADMIN_PASSWORD = "p0o9i8u7";
	public static final String DEFAULT_ADMIN_EMAIL = "ac_eb@hnair.com";
	public static final String DEFAULT_PASSWORD = "Air@pwd123";

	// any constants put here

	private Constants() {
		throw new AssertionError();
	}
}
