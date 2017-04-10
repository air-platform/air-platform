package net.aircommunity.platform.common.base;

/**
 * String utilities without any dependencies.
 * 
 * @author Bin.Zhang
 */
public final class Strings {

	// Represents a failed index search.
	private static final int INDEX_NOT_FOUND = -1;

	/**
	 * Test if a string is null or empty.
	 * 
	 * @param s the string to be tested
	 * @return true if null or empty
	 */
	public static boolean isNullOrEmpty(String s) {
		return s == null || s.trim().equals("");
	}

	/**
	 * <p>
	 * Compares two Strings, returning <code>true</code> if they are equal ignoring the case.
	 * </p>
	 *
	 * <p>
	 * <code>null</code>s are handled without exceptions. Two <code>null</code> references are considered equal.
	 * Comparison is case insensitive.
	 * </p>
	 *
	 * <pre>
	 * Strings.equalsIgnoreCase(null, null)   = true
	 * Strings.equalsIgnoreCase(null, "abc")  = false
	 * Strings.equalsIgnoreCase("abc", null)  = false
	 * Strings.equalsIgnoreCase("abc", "abc") = true
	 * Strings.equalsIgnoreCase("abc", "ABC") = true
	 * </pre>
	 *
	 * @see java.lang.String#equalsIgnoreCase(String)
	 * @param str1 the first String, may be null
	 * @param str2 the second String, may be null
	 * @return <code>true</code> if the Strings are equal, case insensitive, or both <code>null</code>
	 */
	public static boolean equalsIgnoreCase(String str1, String str2) {
		return str1 == null ? str2 == null : str1.equalsIgnoreCase(str2);
	}

	/**
	 * org.apache.commons.lang.Strings
	 * <p>
	 * Checks if a String is not empty (""), not null and not whitespace only.
	 * </p>
	 *
	 * <pre>
	 * Strings.isNotBlank(null)      = false
	 * Strings.isNotBlank("")        = false
	 * Strings.isNotBlank(" ")       = false
	 * Strings.isNotBlank("bob")     = true
	 * Strings.isNotBlank("  bob  ") = true
	 * </pre>
	 *
	 * @param str the String to check, may be null
	 * @return <code>true</code> if the String is not empty and not null and not whitespace
	 */
	public static boolean isNotBlank(String str) {
		return !isBlank(str);
	}

	/**
	 * org.apache.commons.lang.Strings
	 * <p>
	 * Checks if a String is whitespace, empty ("") or null.
	 * </p>
	 *
	 * <pre>
	 * Strings.isBlank(null)      = true
	 * Strings.isBlank("")        = true
	 * Strings.isBlank(" ")       = true
	 * Strings.isBlank("bob")     = false
	 * Strings.isBlank("  bob  ") = false
	 * </pre>
	 *
	 * @param str the String to check, may be null
	 * @return <code>true</code> if the String is null, empty or whitespace
	 */
	public static boolean isBlank(String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if ((Character.isWhitespace(str.charAt(i)) == false)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Strips any of a set of characters from the start and end of a String. This is similar to {@link String#trim()}
	 * but allows the characters to be stripped to be controlled.
	 *
	 * <p>
	 * A {@code null} input String returns {@code null}. An empty string ("") input returns the empty string.
	 * </p>
	 *
	 * <p>
	 * If the stripChars String is {@code null}, whitespace is stripped as defined by
	 * {@link Character#isWhitespace(char)}. Alternatively use {@link #strip(String)}.
	 * </p>
	 *
	 * <pre>
	 * Strings.strip(null, *)          = null
	 * Strings.strip("", *)            = ""
	 * Strings.strip("abc", null)      = "abc"
	 * Strings.strip("  abc", null)    = "abc"
	 * Strings.strip("abc  ", null)    = "abc"
	 * Strings.strip(" abc ", null)    = "abc"
	 * Strings.strip("  abcyx", "xyz") = "  abc"
	 * </pre>
	 *
	 * @param str the String to remove characters from, may be null
	 * @param stripChars the characters to remove, null treated as whitespace
	 * @return the stripped String, {@code null} if null String input
	 */
	public static String strip(String str, final String stripChars) {
		if (isNullOrEmpty(str)) {
			return str;
		}
		str = stripStart(str, stripChars);
		return stripEnd(str, stripChars);
	}

	/**
	 * <p>
	 * Strips any of a set of characters from the start of a String.
	 * </p>
	 *
	 * <p>
	 * A {@code null} input String returns {@code null}. An empty string ("") input returns the empty string.
	 * </p>
	 *
	 * <p>
	 * If the stripChars String is {@code null}, whitespace is stripped as defined by
	 * {@link Character#isWhitespace(char)}.
	 * </p>
	 *
	 * <pre>
	 * Strings.stripStart(null, *)          = null
	 * Strings.stripStart("", *)            = ""
	 * Strings.stripStart("abc", "")        = "abc"
	 * Strings.stripStart("abc", null)      = "abc"
	 * Strings.stripStart("  abc", null)    = "abc"
	 * Strings.stripStart("abc  ", null)    = "abc  "
	 * Strings.stripStart(" abc ", null)    = "abc "
	 * Strings.stripStart("yxabc  ", "xyz") = "abc  "
	 * </pre>
	 *
	 * @param str the String to remove characters from, may be null
	 * @param stripChars the characters to remove, null treated as whitespace
	 * @return the stripped String, {@code null} if null String input
	 */
	public static String stripStart(final String str, final String stripChars) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return str;
		}
		int start = 0;
		if (stripChars == null) {
			while (start != strLen && Character.isWhitespace(str.charAt(start))) {
				start++;
			}
		}
		else if (stripChars.isEmpty()) {
			return str;
		}
		else {
			while (start != strLen && stripChars.indexOf(str.charAt(start)) != INDEX_NOT_FOUND) {
				start++;
			}
		}
		return str.substring(start);
	}

	/**
	 * <p>
	 * Strips any of a set of characters from the end of a String.
	 * </p>
	 *
	 * <p>
	 * A {@code null} input String returns {@code null}. An empty string ("") input returns the empty string.
	 * </p>
	 *
	 * <p>
	 * If the stripChars String is {@code null}, whitespace is stripped as defined by
	 * {@link Character#isWhitespace(char)}.
	 * </p>
	 *
	 * <pre>
	 * Strings.stripEnd(null, *)          = null
	 * Strings.stripEnd("", *)            = ""
	 * Strings.stripEnd("abc", "")        = "abc"
	 * Strings.stripEnd("abc", null)      = "abc"
	 * Strings.stripEnd("  abc", null)    = "  abc"
	 * Strings.stripEnd("abc  ", null)    = "abc"
	 * Strings.stripEnd(" abc ", null)    = " abc"
	 * Strings.stripEnd("  abcyx", "xyz") = "  abc"
	 * Strings.stripEnd("120.00", ".0")   = "12"
	 * </pre>
	 *
	 * @param str the String to remove characters from, may be null
	 * @param stripChars the set of characters to remove, null treated as whitespace
	 * @return the stripped String, {@code null} if null String input
	 */
	public static String stripEnd(final String str, final String stripChars) {
		int end;
		if (str == null || (end = str.length()) == 0) {
			return str;
		}

		if (stripChars == null) {
			while (end != 0 && Character.isWhitespace(str.charAt(end - 1))) {
				end--;
			}
		}
		else if (stripChars.isEmpty()) {
			return str;
		}
		else {
			while (end != 0 && stripChars.indexOf(str.charAt(end - 1)) != INDEX_NOT_FOUND) {
				end--;
			}
		}
		return str.substring(0, end);
	}

	private Strings() {
		throw new AssertionError();
	}

}
