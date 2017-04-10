package net.aircommunity.platform.model;

import java.io.Serializable;

import net.aircommunity.platform.model.constraint.NotEmpty;
import net.aircommunity.platform.model.constraint.Password;

/**
 * Password update request.
 * 
 * @author Bin.Zhang
 */
public class PasswordRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotEmpty
	private String oldPassword;

	@Password
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
