package net.aircommunity.model;

import java.io.Serializable;

import net.aircommunity.model.constraint.NotEmpty;

/**
 * Authentication request via internal or external (3rd party).
 * 
 * @author Bin.Zhang
 */
public class AuthcRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotEmpty
	private String principal;

	@NotEmpty
	private String credential;

	// for external auth
	private long expires;

	// once time password via mobile
	private boolean otp;

	public String getPrincipal() {
		return principal;
	}

	public String getCredential() {
		return credential;
	}

	public long getExpires() {
		return expires;
	}

	public boolean isOtp() {
		return otp;
	}

}
