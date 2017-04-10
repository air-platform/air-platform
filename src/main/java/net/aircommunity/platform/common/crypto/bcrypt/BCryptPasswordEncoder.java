package net.aircommunity.platform.common.crypto.bcrypt;

import java.security.SecureRandom;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.aircommunity.platform.common.crypto.password.PasswordEncoder;

/**
 * Implementation of PasswordEncoder that uses the BCrypt strong hashing function. Clients can optionally supply a
 * "strength" (a.k.a. log rounds in BCrypt) and a SecureRandom instance. The larger the strength parameter the more work
 * will have to be done (exponentially) to hash the passwords. The default value is 10.
 * 
 * copied from org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
 */
public class BCryptPasswordEncoder implements PasswordEncoder {
	private static final Logger LOG = LoggerFactory.getLogger(BCryptPasswordEncoder.class);
	private Pattern BCRYPT_PATTERN = Pattern.compile("\\A\\$2a?\\$\\d\\d\\$[./0-9A-Za-z]{53}");

	private final int strength;

	private final SecureRandom random;

	public BCryptPasswordEncoder() {
		this(-1);
	}

	/**
	 * @param strength the log rounds to use
	 */
	public BCryptPasswordEncoder(int strength) {
		this(strength, null);
	}

	/**
	 * @param strength the log rounds to use
	 * @param random the secure random instance to use
	 */
	public BCryptPasswordEncoder(int strength, SecureRandom random) {
		this.strength = strength;
		this.random = random;
	}

	@Override
	public String encode(CharSequence rawPassword) {
		String salt;
		if (strength > 0) {
			if (random != null) {
				salt = BCrypt.gensalt(strength, random);
			}
			else {
				salt = BCrypt.gensalt(strength);
			}
		}
		else {
			salt = BCrypt.gensalt();
		}
		return BCrypt.hashpw(rawPassword.toString(), salt);
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		if (encodedPassword == null || encodedPassword.length() == 0) {
			LOG.warn("Empty encoded password");
			return false;
		}

		if (!BCRYPT_PATTERN.matcher(encodedPassword).matches()) {
			LOG.warn("Encoded password does not look like BCrypt");
			return false;
		}

		return BCrypt.checkpw(rawPassword.toString(), encodedPassword);
	}
}
