package net.aircommunity.platform.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.PrePersist;

/**
 * Tenant (Jet service provider) profile.
 * 
 * @author Bin.Zhang
 */
@Entity
public class Tenant extends Account {
	private static final long serialVersionUID = 1L;

	// contact email
	@Column(name = "email")
	private String email;

	// vender website
	@Column(name = "website")
	private String website;

	// contact address
	@Column(name = "address")
	private String address;

	// contact hot-line
	@Column(name = "hotline")
	private String hotline;

	// detail description
	@Lob
	@Column(name = "description")
	private String description;

	@PrePersist
	private void beforeSave() {
		role = Role.TENANT;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getHotline() {
		return hotline;
	}

	public void setHotline(String hotline) {
		this.hotline = hotline;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Tenant [email=").append(email).append(", website=").append(website).append(", address=")
				.append(address).append(", hotline=").append(hotline).append(", description=").append(description)
				.append("]");
		return builder.toString();
	}
}
