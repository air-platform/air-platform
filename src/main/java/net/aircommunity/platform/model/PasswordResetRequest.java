package net.aircommunity.platform.model;

import java.io.Serializable;

import io.micro.annotation.constraint.Mobile;
import io.micro.annotation.constraint.NotEmpty;
import io.micro.annotation.constraint.Password;

/**
 * Password reset request via mobile verification code.
 * 
 * @author Bin.Zhang
 */
public class PasswordResetRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	@Mobile
	private String mobile;

	@NotEmpty
	private String verificationCode;

	@Password
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
