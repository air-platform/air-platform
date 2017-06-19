package net.aircommunity.platform.model.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;

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

	// contact email
	@Column(name = "verification")
	private VerificationStatus verification = VerificationStatus.UNVERIFIED;

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

	public Tenant() {
	}

	public Tenant(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public VerificationStatus getVerification() {
		return verification;
	}

	public void setVerification(VerificationStatus verification) {
		this.verification = verification;
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
		builder.append("Tenant [email=").append(email).append(", verification=").append(verification)
				.append(", website=").append(website).append(", address=").append(address).append(", hotline=")
				.append(hotline).append(", description=").append(description).append(", nickName=").append(nickName)
				.append(", role=").append(role).append(", status=").append(status).append(", creationDate=")
				.append(creationDate).append(", avatar=").append(avatar).append(", id=").append(id).append("]");
		return builder.toString();
	}

	/**
	 * A {@code VerificationStatus} represents the various states an Tenant may be in. Tenant MUST be verified before he
	 * can publish any products on the platform
	 */
	public enum VerificationStatus {

		/**
		 * A unverified tenant status
		 */
		UNVERIFIED,

		/**
		 * A verified tenant status
		 */
		VERIFIED;

		public static VerificationStatus fromString(String value) {
			for (VerificationStatus e : values()) {
				if (e.name().equalsIgnoreCase(value)) {
					return e;
				}
			}
			return null;
		}
	}
}