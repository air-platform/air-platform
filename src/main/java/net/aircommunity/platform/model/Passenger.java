package net.aircommunity.platform.model;

import java.io.Serializable;

import javax.persistence.Embeddable;

/**
 * Passenger of an {@code Account}
 * 
 * @author Bin.Zhang
 */
@Embeddable
public class Passenger implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private String mobile;
	// e.g. ID Card number
	private String identity;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Passenger [name=").append(name).append(", mobile=").append(mobile).append(", identity=")
				.append(identity).append("]");
		return builder.toString();
	}

}
