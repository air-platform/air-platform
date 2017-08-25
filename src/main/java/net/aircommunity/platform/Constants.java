package net.aircommunity.platform;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

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
	public static final String TEMPLATE_FEEDBACK_NOTIFICATION = TEMPLATE_DIR + "/mail-feedback.template";
	// @formatter:on
	// template binding keys
	public static final String TEMPLATE_BINDING_USERNAME = "username";
	public static final String TEMPLATE_BINDING_COMPANY = "company";
	public static final String TEMPLATE_BINDING_WEBSITE = "website";
	public static final String TEMPLATE_BINDING_EMAIL = "email";
	public static final String TEMPLATE_BINDING_FAILURE_CAUSE = "cause";
	public static final String TEMPLATE_BINDING_VERIFICATIONLINK = "verificationLink";
	public static final String TEMPLATE_BINDING_RNDPASSWORD = "rndPassword";

	//push notifications
	public static final String TEMPLATE_PUSHNOTIFICATION_ACCOUNTID = "accountid";

	public static final String TEMPLATE_PUSHNOTIFICATION_MESSAGE = "pushMessage";
	public static final String TEMPLATE_PUSHNOTIFICATION_ORDER_MESSAGE = "您的订单状态发生变化，请前往空中社区查看。";
	public static final String TEMPLATE_PUSHNOTIFICATION_POINT_MESSAGE = "您的积分数量发生变化，请前往空中社区查看。";


	public static final String TEMPLATE_PUSHNOTIFICATION_CREATION = "您的订单已经提交，我们正在为您确认运力与价格~";
	public static final String TEMPLATE_PUSHNOTIFICATION_OFFER = "报价已生成，请您确认机型与报价~";

	public static final String TEMPLATE_PUSHNOTIFICATION_OFFER_CONFIRM = "我们已经收到您的确认信息，正在为您安排准备工作~";
	public static final String TEMPLATE_PUSHNOTIFICATION_SIGN = "合同已经签署，为保证您的出行，请尽快完成支付~";
	public static final String TEMPLATE_PUSHNOTIFICATION_PAY = "支付完成，准备起飞，祝您旅途愉快~";
	public static final String TEMPLATE_PUSHNOTIFICATION_TICKET = "出票成功，准备起飞，祝您旅途愉快~";
	public static final String TEMPLATE_PUSHNOTIFICATION_FINISH = "飞行完成，请您对本次飞行进行评价，欢迎您再次惠顾~";


	// any constants put here

	private Constants() {
		throw new AssertionError();
	}
}
