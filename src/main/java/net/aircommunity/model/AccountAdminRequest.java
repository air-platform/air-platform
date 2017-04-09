package net.aircommunity.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import net.aircommunity.model.constraint.Password;
import net.aircommunity.model.constraint.Username;

/**
 * Account registration request by admin via console.
 * 
 * @author Bin.Zhang
 */
public class AccountAdminRequest implements Serializable {
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
