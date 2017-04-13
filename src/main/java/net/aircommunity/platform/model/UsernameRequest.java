package net.aircommunity.platform.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import net.aircommunity.platform.model.constraint.Username;

/**
 * Username update request.
 * 
 * @author Bin.Zhang
 */
public class UsernameRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotNull
	@Username
	private String username;

	public String getUsername() {
		return username;
	}

}
