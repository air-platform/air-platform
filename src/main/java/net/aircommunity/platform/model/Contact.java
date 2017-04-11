package net.aircommunity.platform.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Contact information of an order.
 * 
 * @author Bin.Zhang
 */
@Embeddable
public class Contact implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name = "person")
	private String person;

	@Column(name = "mobile")
	private String mobile;

	@Column(name = "email")
	private String email;

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Contact [person=").append(person).append(", mobile=").append(mobile).append(", email=")
				.append(email).append("]");
		return builder.toString();
	}

}
