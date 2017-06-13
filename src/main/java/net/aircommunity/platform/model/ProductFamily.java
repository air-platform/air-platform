package net.aircommunity.platform.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.aircommunity.platform.model.Product.Category;
import net.aircommunity.platform.model.jaxb.DateTimeAdapter;
import net.aircommunity.platform.model.jaxb.TenantAdapter;

/**
 * Family of an product of a tenant.
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platfrom_product_family", indexes = { @Index(name = "idx_name", columnList = "name"),
		@Index(name = "idx_category", columnList = "category") })
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductFamily extends Reviewable {
	private static final long serialVersionUID = 1L;

	@Size(max = 255)
	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "category")
	@Enumerated(EnumType.STRING)
	private Category category;

	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "creation_date", nullable = false)
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	private Date creationDate;

	@Size(max = 255)
	@Column(name = "image")
	private String image;

	// description
	@Lob
	@Column(name = "description")
	private String description;

	@ManyToOne
	@JoinColumn(name = "tenant_id", nullable = false)
	@XmlJavaTypeAdapter(TenantAdapter.class)
	private Tenant vendor;

	public ProductFamily() {
	}

	public ProductFamily(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Tenant getVendor() {
		return vendor;
	}

	public void setVendor(Tenant vendor) {
		this.vendor = vendor;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProductFamily [name=").append(name).append(", category=").append(category)
				.append(", creationDate=").append(creationDate).append(", image=").append(image)
				.append(", description=").append(description).append(", vendor=").append(vendor)
				.append(", reviewStatus=").append(reviewStatus).append(", rejectedReason=").append(rejectedReason)
				.append(", id=").append(id).append("]");
		return builder.toString();
	}

}
