package net.aircommunity.platform.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.aircommunity.platform.model.jaxb.AccountAdapter;

/**
 * Address of an {@code User}
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platfrom_user_address")
@XmlAccessorType(XmlAccessType.FIELD)
public class Address extends Persistable {
	private static final long serialVersionUID = 1L;

	// contact person
	@Column(name = "contact")
	private String contact;

	// contact person mobile
	@Column(name = "mobile")
	private String mobile;

	// detailed address
	@Column(name = "address")
	private String address;

	@Column(name = "is_default")
	private boolean isDefault;

	@ManyToOne
	@JoinColumn(name = "account_id", nullable = false)
	@XmlJavaTypeAdapter(AccountAdapter.class)
	private User owner;

	public Address() {
	}

	public Address(String id) {
		this.id = id;
	}

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

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Address [contact=").append(contact).append(", mobile=").append(mobile).append(", address=")
				.append(address).append(", isDefault=").append(isDefault).append(", id=").append(id).append("]");
		return builder.toString();
	}
}
