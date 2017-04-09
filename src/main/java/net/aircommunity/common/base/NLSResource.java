package net.aircommunity.common.base;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common superclass for all message bundle classes.
 * <p>
 * Provides convenience methods for manipulating messages. The {@code #bind} methods perform string substitution and
 * should be considered a convenience and not a full substitute replacement for {@code MessageFormat#format} method
 * calls. Text appearing within curly braces in the given message, will be interpreted as a numeric index to the
 * corresponding substitution object in the given array. Calling the #bind methods with text that does not map to an
 * integer will result in an java.lang.IllegalArgumentException. Text appearing within single quotes is treated as a
 * literal. A single quote is escaped by a proceeding single quote. Clients who wish to use the full substitution power
 * of the MessageFormat class should call that class directly and not use these #bind methods.<br>
 * 
 * <p>
 * Note: the field of a class should be public static. <br>
 * The key for a field should follow this pattern: <br>
 * Field: {@code public static String KEY_A;}<br>
 * Key in properties file: key.a=what ever value. (all lowercase and underscore(_) will be replaced with dot(.)
 * 
 * <p>
 * Clients may subclass this type. (NLS: Need Localize String)
 * 
 * @author Bin.Zhang
 */
public abstract class NLSResource {
	private static final Logger LOG = LoggerFactory.getLogger(NLSResource.class);

	// masks
	private static final int MOD_EXPECTED = Modifier.PUBLIC | Modifier.STATIC;
	private static final int MOD_MASK = MOD_EXPECTED | Modifier.FINAL;

	/**
	 * This is the default resolve if omit
	 * 
	 * e.g. class field: MSG_KEY_1 mapped to msg.key.1 in properties file
	 */
	public static final KeyResolver LOWERCASE_RESOLVER = new LowerCaseResolver();

	/**
	 * e.g. class field: MSG_KEY_1 mapped to MSG.KEY.1 in properties file
	 */
	public static final KeyResolver NORMAL_RESOLVER = new NormalResolver();

	/**
	 * e.g. class field: MSG_KEY_1 mapped to MSG_KEY_1 in properties file
	 */
	public static final KeyResolver NOOP_RESOLVER = new NoopResolver();

	/**
	 * The default resolve
	 */
	public static final KeyResolver DEFAULT_RESOLVER = LOWERCASE_RESOLVER;

	/**
	 * Bind the given message's substitution with the given bindings and default locale <br>
	 * 
	 * @param baseName the base name of the resource bundle, a fully qualified class name
	 * @param messageId the message ID in the resource bundle to be manipulated
	 * @param bindings the object to be inserted into the message
	 * @return the manipulated String
	 * @throws IllegalArgumentException if message and bindings array is not of the type expected by the format
	 * element(s) that use it.
	 */
	public final static String bind(String baseName, String messageId, Object... bindings) {
		return bind(baseName, Locale.getDefault(), messageId, bindings);
	}

	/**
	 * Bind the given message's substitution with the given bindings<br>
	 * 
	 * @param baseName the base name of the resource bundle, a fully qualified class name
	 * @param locale locale for the message
	 * @param message the message ID in the resource bundle to be manipulated
	 * @param bindings the object to be inserted into the message
	 * @return the manipulated String
	 * @throws IllegalArgumentException if messageId and bindings array is not of the type expected by the format
	 * element(s) that use it.
	 */
	public final static String bind(String baseName, Locale locale, String messageId, Object... bindings) {
		ResourceBundle rb = null;
		try {
			rb = ResourceBundle.getBundle(baseName, locale == null ? Locale.getDefault() : locale);
		}
		catch (MissingResourceException e) {
			LOG.warn("Missing resource bundle '{}' for locale '{}', fallback to {}", baseName, locale, Locale.ENGLISH);
			rb = ResourceBundle.getBundle(baseName, Locale.ENGLISH);
		}
		try {
			String message = rb.getString(messageId);
			return MessageFormat.format(message, bindings);
		}
		catch (MissingResourceException e) {
			LOG.warn("Missing message for '{}' in resource bundle '{}'!", messageId, baseName);
		}
		return null;
	}

	/**
	 * Initialize the given class with the resource keys generated from the class field with default {@code KeyResolver}
	 * 
	 * @param clazz the class where the constants will exist
	 */
	public static void initialize(Class<?> clazz) {
		initialize(clazz, DEFAULT_RESOLVER);
	}

	/**
	 * Initialize the given class with the resource keys generated from the class field using {@code KeyResolver}
	 * 
	 * @param clazz the class where the constants will exist
	 * @param resolvers the resolvers to resolve the key in the properties file, use defaults if null
	 */
	public static void initialize(Class<?> clazz, KeyResolver resolver) {
		resolve(clazz, resolver);
	}

	private static void resolve(Class<?> clazz, KeyResolver keyResolver) {
		final boolean isAccessible = (clazz.getModifiers() & Modifier.PUBLIC) != 0;
		final KeyResolver resolver = keyResolver == null ? DEFAULT_RESOLVER : keyResolver;

		for (Field field : clazz.getDeclaredFields()) {
			// can only set value of public static non-final fields
			if (!((field.getModifiers() & MOD_MASK) == MOD_EXPECTED && field.getType() == String.class)) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Not valid field '{}', ignore it(expect public static final String field).",
							field.getName());
				}
				continue;
			}

			// try to resolve field to generate a key which is used in the resource bundle
			String key = resolver.resolve(field);

			try {
				// Check to see if we are allowed to modify the field. If we aren't (for instance
				// if the class is not public) then change the accessible attribute of the field
				// before trying to set the value.
				if (!isAccessible) {
					field.setAccessible(true);
				}
				// Set the value into the field. We should never get an exception here because
				// we know we have a public static non-final field. If we do get an exception, silently
				// LOG it and continue. This means that the field will (most likely) be un-initialized and
				// will fail later in the code and if so then we will see both the NPE and this error.
				field.set(null, key);
			}
			catch (Exception e) {
				LOG.error("Exception when setting a field value.", e);
			}
		}
	}

	/**
	 * The key resolver used to resolved keys
	 */
	public static interface KeyResolver {

		/**
		 * @param field the field of message class
		 * @return the resolved key for resource bundle
		 */
		String resolve(Field field);
	}

	// ****************************************************************
	// default resolvers
	// ****************************************************************

	private static class LowerCaseResolver implements KeyResolver {
		@Override
		public String resolve(Field field) {
			return field.getName().replace("_", ".").toLowerCase();
		}
	}

	private static class NormalResolver implements KeyResolver {
		@Override
		public String resolve(Field field) {
			return field.getName().replace("_", ".");
		}
	}

	private static class NoopResolver implements KeyResolver {
		@Override
		public String resolve(Field field) {
			return field.getName();
		}
	}

}
