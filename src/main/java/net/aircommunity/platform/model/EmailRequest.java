package net.aircommunity.platform.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import net.aircommunity.platform.model.constraint.Email;

/**
 * Email update request.
 * 
 * @author Bin.Zhang
 */
public class EmailRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	@Email
	@NotNull
	private String email;

	public String getEmail() {
		return email;
	}

}
