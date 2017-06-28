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
 * Password reset request via mobile verification code.
 * 
 * @author Bin.Zhang
 */
@Immutable
@XmlAccessorType(XmlAccessType.FIELD)
public class PasswordResetRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	@Mobile
	@NotNull
	private String mobile;

	@NotEmpty
	@NotNull
	private String verificationCode;

	@Password
	@NotNull
	private String newPassword;

	public String getMobile() {
		return mobile;
	}

	public String getVerificationCode() {
		return verificationCode;
	}

	public String getNewPassword() {
		return newPassword;
	}

}
