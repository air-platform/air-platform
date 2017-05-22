package net.aircommunity.platform.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import io.micro.annotation.constraint.NotEmpty;
import io.micro.annotation.constraint.Password;

/**
 * Password update request.
 * 
 * @author Bin.Zhang
 */
@XmlAccessorType(XmlAccessType.FIELD)
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
