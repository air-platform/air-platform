package net.aircommunity.platform;

/**
 * Constants for the whole AIR platform (internal use only).
 * 
 * @author Bin.Zhang
 */
@SuppressWarnings("javadoc")
public final class Constants {

	private static final String TEMPLATE_DIR = "template";
	public static final String TEMPLATE_MAIL_VERIFICATION = TEMPLATE_DIR + "/mail-verification.template";
	public static final String TEMPLATE_MAIL_RESET_PASSOWRD = TEMPLATE_DIR + "/mail-reset-password.template";
	public static final String TEMPLATE_MAIL_ORDER_NOTIFICATION = TEMPLATE_DIR + "/mail-%s-order.template";

	// client managers of a product
	// MUST IN FORMAT OF: person1:email1, person2:email2, ..., personN:emailN
	public static final String CONTACT_INFO_SEPARATOR = ":";
	public static final String CONTACT_SEPARATOR = ",";

	//
	public static final String CLAIM_API_KEY = "claim.apikey";
	public static final String DEFAULT_ADMIN_USERNAME = "admin";
	public static final String DEFAULT_ADMIN_PASSWORD = "p0o9i8u7";
	public static final String DEFAULT_ADMIN_EMAIL = "admin@aircommunity.net";
	public static final String DEFAULT_PASSWORD = "Air@pwd123";

	// any constants put here

	private Constants() {
		throw new AssertionError();
	}
}
