package net.aircommunity.platform.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import net.aircommunity.platform.model.constraint.Password;
import net.aircommunity.platform.model.constraint.Username;

/**
 * Account registration request by admin/tenant via console.
 * 
 * @author Bin.Zhang
 */
public class AccountRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotNull
	@Username
	private String username;

	@NotNull
	@Password
	private String password;

	@NotNull
	private Role role;

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public Role getRole() {
		return role;
	}

}
