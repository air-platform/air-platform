package net.aircommunity.platform.model;

import java.io.Serializable;

import javax.annotation.concurrent.Immutable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Authentication Access Token (e.g. JWT, ApiKey etc.)
 * 
 * @author Bin.Zhang
 */
@Immutable
@XmlAccessorType(XmlAccessType.FIELD)
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
