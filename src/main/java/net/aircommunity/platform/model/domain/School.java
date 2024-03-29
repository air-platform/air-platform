package net.aircommunity.platform.model.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import io.micro.annotation.constraint.NotEmpty;
import net.aircommunity.platform.model.jaxb.TenantAdapter;

/**
 * School of a {@code Tenant}.
 * 
 * @author guankai
 */
@Entity
@Table(name = "air_platform_school")
@XmlAccessorType(XmlAccessType.FIELD)
public class School extends Persistable {
	private static final long serialVersionUID = 1L;

	@NotEmpty
	@Size(max = SCHOOL_NAME_LEN)
	@Column(name = "name", length = SCHOOL_NAME_LEN, nullable = false)
	private String name;

	@Size(max = IMAGE_URL_LEN)
	@Column(name = "image", length = IMAGE_URL_LEN)
	private String image;

	@NotEmpty
	@Size(max = ADDRESS_LEN)
	@Column(name = "address", length = ADDRESS_LEN)
	private String address;

	@NotEmpty
	@Size(max = CONTACT_LEN)
	@Column(name = "contact", length = CONTACT_LEN)
	private String contact;

	@Lob
	@Column(name = "description")
	private String description;

	@Lob
	@Column(name = "base_description")
	private String baseDescription;

	@ManyToOne
	@JoinColumn(name = "tenant_id", nullable = false)
	@XmlJavaTypeAdapter(TenantAdapter.class)
	private Tenant vendor;

	public School() {
	}

	public School(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getBaseDescription() {
		return baseDescription;
	}

	public void setBaseDescription(String baseDescription) {
		this.baseDescription = baseDescription;
	}

	public Tenant getVendor() {
		return vendor;
	}

	public void setVendor(Tenant vendor) {
		this.vendor = vendor;
	}

	/**
	 * Test if current entity is the owner of the given tenant.
	 */
	public boolean isOwner(String tenantId) {
		return vendor.getId().equals(tenantId);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("School [name=").append(name).append(", image=").append(image).append(", address=")
				.append(address).append(", contact=").append(contact).append(", description=").append(description)
				.append(", baseDescription=").append(baseDescription).append(", id=").append(id).append("]");
		return builder.toString();
	}

}
