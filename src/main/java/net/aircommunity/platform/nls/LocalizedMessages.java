package net.aircommunity.platform.nls;

import java.util.Locale;

import net.aircommunity.platform.common.base.NLSResource;

/**
 * NLS Messages.
 * 
 * @author Bin.Zhang
 */
public class LocalizedMessages extends NLSResource {

	// NLS resource bundle
	private static final String RESOURCE_BUNDLE_BASENAME = LocalizedMessages.class.getPackage().getName() + ".message";

	public static String SYS_FALLBACK_DEFAULT_SPEECH;
	public static String USR_FALLBACK_DEFAULT_SPEECHES;
	public static String PROMPT_UNSUPPORTED_LANGUAGE;
	public static String PROMPT_MISSING_PARAMETER;

	// internal server error
	public static String INTERNAL_SERVER_ERROR;

	/**
	 * Bind the messageId with bindings.
	 * 
	 * @param messageId the messageId
	 * @param bindings the bindings
	 * @return localized message
	 */
	public static String bind(String messageId, Object... bindings) {
		return bind(RESOURCE_BUNDLE_BASENAME, Locale.getDefault(), messageId, bindings);
	}

	/**
	 * Bind the messageId with bindings.
	 * 
	 * @param locale the locale
	 * @param messageId the messageId
	 * @param bindings the bindings
	 * @return localized message
	 */
	public static String bind(Locale locale, String messageId, Object... bindings) {
		return bind(RESOURCE_BUNDLE_BASENAME, locale, messageId, bindings);
	}

	static {
		// initialize message binding from the message.properties file
		initialize(LocalizedMessages.class);
	}
}
