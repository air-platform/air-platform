package net.aircommunity.platform.model;

import java.util.HashMap;

/**
 * Authentication Context.
 * 
 * @author Bin.Zhang
 */
@SuppressWarnings("unchecked")
public class AuthContext extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;

	private static final String ATTR_SOURCE = "context.source"; // user-agent
	private static final String ATTR_IP = "context.ip";
	private static final String ATTR_IS_OTP = "context.isOtp";
	private static final String ATTR_EXPIRY = "context.expiry";

	public String source() {
		return (String) get(ATTR_SOURCE);
	}

	public AuthContext source(String source) {
		put(ATTR_SOURCE, source);
		return this;
	}

	public String ipAddress() {
		return (String) get(ATTR_IP);
	}

	public AuthContext ipAddress(String ip) {
		put(ATTR_IP, ip);
		return this;
	}

	/**
	 * The account authentication expiry.
	 * 
	 * @return the expiry
	 */
	public long expiry() {
		return (long) get(ATTR_EXPIRY);
	}

	public AuthContext expiry(long expiry) {
		put(ATTR_EXPIRY, expiry);
		return this;
	}

	/**
	 * Whether is one time password or not (dynamic code via mobile)
	 * 
	 * @return otp or not
	 */
	public boolean isOtp() {
		return (boolean) get(ATTR_IS_OTP);
	}

	public AuthContext otp(boolean isOtp) {
		put(ATTR_IS_OTP, isOtp);
		return this;
	}

	public <T> T attribute(String key) {
		Object value = get(key);
		return (T) value;
	}

	public AuthContext attribute(String key, Object value) {
		put(key, value);
		return this;
	}

}
