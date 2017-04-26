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
