package net.aircommunity.platform.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.micro.annotation.constraint.Password;
import io.micro.annotation.constraint.Username;


/**
 * Account (tenant/user) registration request by admin via console.
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
