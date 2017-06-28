package net.aircommunity.platform.model;

import java.io.Serializable;

import javax.annotation.concurrent.Immutable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import io.micro.annotation.constraint.NotEmpty;

/**
 * Authentication request via internal or external (3rd party).
 * 
 * @author Bin.Zhang
 */
@Immutable
@XmlAccessorType(XmlAccessType.FIELD)
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AuthcRequest [principal=").append(principal).append(", credential=********")
				.append(", expires=").append(expires).append(", otp=").append(otp).append("]");
		return builder.toString();
	}

}
