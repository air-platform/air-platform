package net.aircommunity.platform.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import io.micro.annotation.constraint.NotEmpty;
import net.aircommunity.platform.model.jaxb.AccountAdapter;

/**
 * Created by guankai on 11/04/2017.
 */
@Entity
@Table(name = "air_platform_school")
@XmlAccessorType(XmlAccessType.FIELD)
public class School extends Persistable {
	private static final long serialVersionUID = 1L;

	@NotEmpty
	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "image")
	private String image;

	@NotEmpty
	@Column(name = "address")
	private String address;

	@NotEmpty
	@Column(name = "contact")
	private String contact;

	@Lob
	@Column(name = "description")
	private String description;
	@Lob
	@Column(name = "base_desc")
	private String baseDesc;

	@XmlJavaTypeAdapter(AccountAdapter.class)
	@ManyToOne
	@JoinColumn(name = "tenant_id", nullable = false)
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

	public Tenant getVendor() {
		return vendor;
	}

	public void setVendor(Tenant vendor) {
		this.vendor = vendor;
	}

	public String getBaseDesc() {
		return baseDesc;
	}

	public void setBaseDesc(String baseDesc) {
		this.baseDesc = baseDesc;
	}
}
