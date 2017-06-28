package net.aircommunity.platform.model.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 * Tenant (service provider).
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platform_tenant", indexes = { @Index(name = "idx_role", columnList = "role") })
public class Tenant extends Account {
	private static final long serialVersionUID = 1L;

	// contact email
	@Size(max = 255)
	@Column(name = "email")
	private String email;

	// verification status
	@Column(name = "verification", nullable = false, length = 15)
	@Enumerated(EnumType.STRING)
	private VerificationStatus verification = VerificationStatus.UNVERIFIED;

	// vender website
	@Size(max = 255)
	@Column(name = "website")
	private String website;

	// contact address
	@Size(max = 1024)
	@Column(name = "address", length = 1024)
	private String address;

	// contact hot-line
	@Size(max = 255)
	@Column(name = "hotline")
	private String hotline;

	// details fo this tenant
	@Lob
	@Column(name = "description")
	private String description;

	public Tenant() {
		super();
	}

	public Tenant(String id) {
		super(id);
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
