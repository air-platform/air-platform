package net.aircommunity.platform.model.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.aircommunity.platform.model.jaxb.VenueCategoryAdapter;
import net.aircommunity.platform.model.jaxb.VenueProductAdapter;

/**
 * @author Xiangwen.Kong
 */
@Entity
@Table(name = "air_platform_venue_category_product")
@XmlAccessorType(XmlAccessType.FIELD)
public class VenueCategoryProduct extends Persistable {
	private static final long serialVersionUID = 1L;

	@ManyToOne
	@JoinColumn(name = "venue_category_id")
	@XmlJavaTypeAdapter(VenueCategoryAdapter.class)
	protected VenueCategory venueCategory;

	// CharterOrder charterOrder = CharterOrder.class.cast(order);
	@ManyToOne
	@JoinColumn(name = "product_id")
	//@XmlTransient
	@XmlJavaTypeAdapter(VenueProductAdapter.class)
	@XmlElement(name = "productInfo")
	protected Product product;

	@Transient
	@XmlElement(name = "product")
	private String productId;

	public String getProductId() {
		return productId;
	}
	@PostLoad
	private void onLoad() {
		productId = product.getId();
	}

	// venue category picture
	@Column(name = "type", length = PRODUCT_TYPE_LEN)
	private String type;


	public VenueCategoryProduct() {
	}

	public VenueCategoryProduct(String id) {
		this.id = id;
	}

	/*
	 * public VenueCategoryProduct(String id1, String id2) { this.venueCategory.id = id1; this.product.id = id2;
	 * this.type = type; }
	 */

	public VenueCategory getVenueCategory() {
		return venueCategory;
	}

	public void setVenueCategory(VenueCategory venueCategory) {
		this.venueCategory = venueCategory;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("VenueCategoryProduct [id=").append(id).append("]");
		return builder.toString();
	}

}
