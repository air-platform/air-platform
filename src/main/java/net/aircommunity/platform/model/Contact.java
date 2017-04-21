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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((mobile == null) ? 0 : mobile.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Contact other = (Contact) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		}
		else if (!email.equals(other.email))
			return false;
		if (mobile == null) {
			if (other.mobile != null)
				return false;
		}
		else if (!mobile.equals(other.mobile))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Contact [person=").append(person).append(", mobile=").append(mobile).append(", email=")
				.append(email).append("]");
		return builder.toString();
	}

}
