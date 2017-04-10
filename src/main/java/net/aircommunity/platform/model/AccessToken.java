package net.aircommunity.platform.model;

import java.io.Serializable;

/**
 * Authentication Access Token (e.g. JWT, ApiKey etc.)
 * 
 * @author Bin.Zhang
 */
public class AccessToken implements Serializable {
	private static final long serialVersionUID = 1L;

	private final String token;

	public AccessToken(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}

}
