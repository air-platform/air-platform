package net.aircommunity.platform.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.micro.annotation.constraint.NotEmpty;
import io.micro.annotation.constraint.Password;

/**
 * Password update request.
 * 
 * @author Bin.Zhang
 */
public class PasswordRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotEmpty
	@NotNull
	private String oldPassword;

	@Password
	@NotNull
	private String newPassword;

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

}
