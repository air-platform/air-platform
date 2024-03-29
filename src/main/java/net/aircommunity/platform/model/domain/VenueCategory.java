package net.aircommunity.platform.model.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import io.micro.annotation.constraint.NotEmpty;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import net.aircommunity.platform.model.domain.Product.Category;
import net.aircommunity.platform.model.jaxb.VenueInfoAdapter;

/**
 * @author Xiangwen.Kong
 */
@Entity
@Table(name = "air_platform_venue_category")
@XmlAccessorType(XmlAccessType.FIELD)
public class VenueCategory extends Persistable {
	private static final long serialVersionUID = 1L;

	// venue category name
	@NotEmpty
	@Size(max = PRODUCT_NAME_LEN)
	@Column(name = "name", length = PRODUCT_NAME_LEN, nullable = false)
	private String name;

	// venue category picture
	@Column(name = "picture", length = IMAGE_URL_LEN)
	private String picture;

	// venue category description
	@Lob
	@Column(name = "description")
	private String description;

	// @XmlElement
	@ManyToOne
	@JoinColumn(name = "venue_info_id")
	@XmlJavaTypeAdapter(VenueInfoAdapter.class)
	protected VenueInfo venueInfo;

	@OneToMany(mappedBy = "venueCategory", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private Set<VenueCategoryProduct> venueCategoryProducts = new HashSet<>();

	@Enumerated(EnumType.STRING)
	@Column(name = "category", length = PRODUCT_CATEGORY_LEN)
	protected Category category = Category.AIR_VENUE;

	public VenueCategory(String id) {
		this.id = id;
	}

	public VenueCategory() {
	}

	public Set<VenueCategoryProduct> getVenueCategoryProducts() {
		return venueCategoryProducts;
	}

	public void setVenueCategoryProducts(Set<VenueCategoryProduct> products) {
		if (products != null) {
			products.stream().forEach(product -> product.setVenueCategory(this));
			venueCategoryProducts.clear();
			venueCategoryProducts.addAll(products);
		}
	}

	public VenueInfo getVenueInfo() {
		return venueInfo;
	}

	public void setVenueInfo(VenueInfo venueInfo) {
		this.venueInfo = venueInfo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
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
		builder.append("VenueCategory [id=").append(id).append(", name=").append(name).append(", picture=")
				.append(picture).append(", description=").append(description).append("]");
		return builder.toString();
	}

}
