package net.aircommunity.model;

import java.io.Serializable;

import net.aircommunity.model.constraint.Email;

/**
 * Email update request.
 * 
 * @author Bin.Zhang
 */
public class EmailRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	@Email
	private String email;

	public String getEmail() {
		return email;
	}

}
