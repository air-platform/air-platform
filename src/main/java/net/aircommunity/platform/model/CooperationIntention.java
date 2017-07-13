package net.aircommunity.platform.model;

import java.io.Serializable;

import io.micro.annotation.constraint.Email;
import io.micro.annotation.constraint.NotEmpty;

/**
 * Business cooperation intention.
 * 
 * @author Bin.Zhang
 */
public class CooperationIntention implements Serializable {
	private static final long serialVersionUID = 1L;

	// intention category
	@NotEmpty
	private String category;

	@NotEmpty
	private String compay;

	@NotEmpty
	private String phone;

	@Email
	private String email;

	@NotEmpty
	private String content;

	public String getCategory() {
		return category;
	}

	public String getCompay() {
		return compay;
	}

	public String getPhone() {
		return phone;
	}

	public String getEmail() {
		return email;
	}

	public String getContent() {
		return content;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CooperationIntention [category=").append(category).append(", compay=").append(compay)
				.append(", phone=").append(phone).append(", email=").append(email).append(", content=").append(content)
				.append("]");
		return builder.toString();
	}
}
