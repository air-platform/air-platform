package net.aircommunity.platform.model;

import java.io.Serializable;

import javax.annotation.concurrent.Immutable;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import io.micro.annotation.constraint.Mobile;
import io.micro.annotation.constraint.NotEmpty;
import io.micro.annotation.constraint.Password;

/**
 * Account registration request by user via mobile.
 * 
 * @author Bin.Zhang
 */
@Immutable
@XmlAccessorType(XmlAccessType.FIELD)
public class UserAccountRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotNull
	@Mobile
	private String mobile;

	@NotNull
	@NotEmpty
	private String verificationCode;

	@NotNull
	@Password
	private String password;

	public String getMobile() {
		return mobile;
	}

	public String getVerificationCode() {
		return verificationCode;
	}

	public String getPassword() {
		return password;
	}

}
