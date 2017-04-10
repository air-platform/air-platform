package net.aircommunity.platform.model.constraint;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validations utility.
 * 
 * @author Bin.Zhang
 */
public final class Validations {

	private static String ATOM = "[^\\x00-\\x1F^\\(^\\)^\\<^\\>^\\@^\\,^\\;^\\:^\\\\^\\\"^\\.^\\[^\\]^\\s]";
	private static String DOMAIN = "(" + ATOM + "+(\\." + ATOM + "+)*";
	private static String IP_DOMAIN = "\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\]";

	/**
	 * 正则：手机号（精确）
	 * <p>
	 * 移动：134(0-8)、135、136、137、138、139、147、150、151、152、157、158、159、178、182、183、184、187、188
	 * </p>
	 * <p>
	 * 联通：130、131、132、145、155、156、175、176、185、186
	 * </p>
	 * <p>
	 * 电信：133、153、173、177、180、181、189
	 * </p>
	 * <p>
	 * 全球星：1349
	 * </p>
	 * <p>
	 * 虚拟运营商：170
	 * </p>
	 */
	public static final String EXACT_MOBILE = "^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,3,5-8])|(18[0-9])|(147))\\d{8}$";

	private static final Pattern EMAIL_PATTERN;
	private static final Pattern USERNAME_PATTERN;
	private static final Pattern EXACT_MOBILE_PATTERN;

	static {
		EMAIL_PATTERN = Pattern.compile("^" + ATOM + "+(\\." + ATOM + "+)*@" + DOMAIN + "|" + IP_DOMAIN + ")$",
				Pattern.CASE_INSENSITIVE);

		// CN, number, alphabet, underscore(_), hyphen (-)
		USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_-\u4e00-\u9fa5]{4,20}$"); // length: 4-20
		EXACT_MOBILE_PATTERN = Pattern.compile(EXACT_MOBILE);
	}

	/**
	 * Learn whether a given object is a valid email address.
	 * 
	 * @param value to check
	 * @return <code>true</code> if the validation passes
	 */
	public static boolean isValidEmail(CharSequence email) {
		return isValid(email, EMAIL_PATTERN);
	}

	/**
	 * Learn whether a given object is a valid username.
	 * 
	 * @param value to check
	 * @return <code>true</code> if the validation passes
	 */
	public static boolean isValidUsername(CharSequence username) {
		return isValid(username, USERNAME_PATTERN);
	}

	/**
	 * Learn whether a given object is a valid mobile (in China).
	 * 
	 * @param value to check
	 * @return <code>true</code> if the validation passes
	 */
	public static boolean isValidChineseMobile(CharSequence mobile) {
		return isValid(mobile, EXACT_MOBILE_PATTERN);
	}

	/**
	 * Learn whether a particular value matches a given pattern per {@link Matcher#matches()}.
	 * 
	 * @param value
	 * @param aPattern
	 * @return <code>true</code> if <code>value</code> was a <code>String</code> matching <code>aPattern</code>
	 */
	// TODO it would seem to make sense to move or reduce the visibility of this
	// method as it is more general than email.
	private static boolean isValid(Object value, Pattern aPattern) {
		if (value == null) {
			return true;
		}
		if (!(value instanceof CharSequence)) {
			return false;
		}
		CharSequence seq = (CharSequence) value;
		if (seq.length() == 0) {
			return true;
		}
		return aPattern.matcher(seq).matches();
	}

	private Validations() {
		throw new AssertionError();
	}

}
