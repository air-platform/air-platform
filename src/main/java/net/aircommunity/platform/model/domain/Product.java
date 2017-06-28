package net.aircommunity.platform.model.domain;

import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonView;

import io.micro.annotation.constraint.NotEmpty;
import io.micro.common.Strings;
import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.constraint.ContactList;
import net.aircommunity.platform.model.jaxb.DateTimeAdapter;
import net.aircommunity.platform.model.jaxb.TenantAdapter;

/**
 * Product of an {@code Tenant} (AKA. vendor).
 * 
 * @author Bin.Zhang
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class Product extends Reviewable {
	private static final long serialVersionUID = 1L;

	/**
	 * Lowest product rank
	 */
	public static final int LOWEST_RANK = 0;

	/**
	 * Default product rank
	 */
	public static final int DEFAULT_RANK = LOWEST_RANK;

	// product name
	@NotEmpty
	@Size(max = 255)
	@Column(name = "name", nullable = false)
	protected String name;

	// product category
	@Column(name = "category", length = 20, nullable = false)
	@Enumerated(EnumType.STRING)
	protected Category category;

	// product image
	@Size(max = 255)
	@Column(name = "image")
	protected String image;

	// product score
	@Column(name = "score", nullable = false)
	protected double score = 0.0;

	// TODO useful?
	// @Column(name = "discount", nullable = true)
	// protected double discount;

	// total sales count
	@Column(name = "total_sales")
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	protected int totalSales = 0;

	// product rank (high rank will considered as hot, sort by rank DESC, 0 will be the lowest rank)
	@Min(LOWEST_RANK)
	@Column(name = "rank")
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	protected int rank = DEFAULT_RANK;

	// whether it can be published or not
	// e.g. product put on sale/pull off shelves
	// XXX NOTE: a product is published, it means it's already reviewed with APPROVED
	@Column(name = "published", nullable = false)
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	protected boolean published = false;

	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "creation_date", nullable = false)
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	protected Date creationDate;

	// client managers for this product
	// MUST IN FORMAT OF: person1:email1, person2:email2, ..., personN:emailN
	@ContactList
	@Lob
	@Column(name = "client_managers")
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	protected String clientManagers;

	// product description
	@Lob
	@Column(name = "description")
	protected String description;

	@ManyToOne
	@JoinColumn(name = "tenant_id", nullable = false)
	@XmlJavaTypeAdapter(TenantAdapter.class)
	protected Tenant vendor;

	@PostLoad
	private void onLoad() {
		setClientManagers(clientManagers);
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

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public int getTotalSales() {
		return totalSales;
	}

	public void setTotalSales(int totalSales) {
		this.totalSales = totalSales;
	}

	public boolean isPublished() {
		return published;
	}

	public void setPublished(boolean published) {
		this.published = published;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getClientManagers() {
		return clientManagers;
	}

	public void setClientManagers(String clientManagers) {
		this.clientManagers = Contact.normalizeContacts(clientManagers);
	}

	public Set<Contact> getClientManagerContacts() {
		if (Strings.isBlank(clientManagers)) {
			return Collections.emptySet();
		}
		return Contact.parseContacts(clientManagers);
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

	// product type
	public abstract Type getType();

	/**
	 * Product type
	 */
	public enum Type {
		FLEET, FERRYFLIGHT, JETTRAVEL, AIRTAXI, AIRTOUR, AIRTRANSPORT, COURSE;

		public static Type fromString(String value) {
			for (Type e : values()) {
				if (e.name().equalsIgnoreCase(value)) {
					return e;
				}
			}
			return null;
		}

		// @JsonValue
		// public String toString() {
		// return super.toString().toLowerCase();
		// }
	}

	/**
	 * Product Category
	 */
	public enum Category {
		NONE, AIR_JET, AIR_TOUR, AIR_TAXI, AIR_TRANS, AIR_TRAINING;

		private static Set<Category> categories;

		public static Category fromString(String value) {
			for (Category e : values()) {
				if (e.name().equalsIgnoreCase(value)) {
					return e;
				}
			}
			return null;
		}

		public static Set<Category> categories() {
			if (categories == null) {
				categories = Stream.of(values()).filter(category -> category != Category.NONE)
						.collect(Collectors.toSet());
			}
			return categories;
		}

	}
}
