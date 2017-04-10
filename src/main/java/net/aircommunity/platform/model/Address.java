package net.aircommunity.platform.model;

import java.io.Serializable;

import javax.persistence.Embeddable;

/**
 * Address of an {@code Account}
 * 
 * @author Bin.Zhang
 */
@Embeddable
public class Address implements Serializable {
	private static final long serialVersionUID = 1L;

	// contact person
	private String contact;

	// contact person mobile
	private String mobile;

	// detailed address
	private String address;

	private boolean isDefault;

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

}
