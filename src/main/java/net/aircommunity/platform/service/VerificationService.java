package net.aircommunity.platform.service;

import javax.annotation.Nonnull;

/**
 * Verification service verify code
 * 
 * @author Bin.Zhang
 */
public interface VerificationService {

	/**
	 * Generate a verification code.
	 * 
	 * @param key the key to generate the code for
	 * @param ip the ip address
	 * @param expires the code expires in seconds
	 * @return verification code generated
	 */
	@Nonnull
	String generateCode(@Nonnull String key, @Nonnull String ip, long expires);

	@Nonnull
	default String generateCode(String key, @Nonnull String ip) {
		// defaults to 120 seconds
		return generateCode(key, ip, 120);
	}

	/**
	 * Verify if the code is correct for the given key.
	 * 
	 * @param key the key to lookup the code
	 * @param code verification code
	 * @return true if code is correct, false otherwise
	 */
	boolean verifyCode(@Nonnull String key, @Nonnull String code);

}
