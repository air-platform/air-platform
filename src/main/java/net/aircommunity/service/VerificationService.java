package net.aircommunity.service;

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
	 * @param expires the code exipry in seconds
	 * @return verification code generated
	 */
	@Nonnull
	String generateCode(@Nonnull String key, long expires);

	/**
	 * Verify if the code is correct for the given key.
	 * 
	 * @param key the key to lookup the code
	 * @param code verification code
	 * @return true if code is correct, false otherwise
	 */
	boolean verifyCode(@Nonnull String key, @Nonnull String code);

}
