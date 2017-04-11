package net.aircommunity.platform.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import net.aircommunity.platform.model.constraint.Mobile;
import net.aircommunity.platform.model.constraint.NotEmpty;
import net.aircommunity.platform.model.constraint.Password;

/**
 * Account registration request by user via mobile.
 * 
 * @author Bin.Zhang
 */
public class UserAccountRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotNull
	@Mobile
	private String mobile;

	@NotEmpty
	private String verificationCode;

	@NotNull
	@Password
	private String password;

	@NotNull
	private Role role; // TODO REMOVE ROLE?

	public String getMobile() {
		return mobile;
	}

	public String getVerificationCode() {
		return verificationCode;
	}

	public String getPassword() {
		return password;
	}

	public Role getRole() {
		return role;
	}

}
