package net.aircommunity.platform.model.domain;

import com.fasterxml.jackson.annotation.JsonView;
import io.micro.annotation.constraint.NotEmpty;
import io.micro.common.Strings;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
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
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import net.aircommunity.platform.model.CurrencyUnit;
import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.constraint.ContactList;
import net.aircommunity.platform.model.jaxb.DateTimeAdapter;
import net.aircommunity.platform.model.jaxb.TenantAdapter;
import org.hibernate.annotations.ColumnDefault;

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
	 * Unlimited product stock
	 */
	public static final int UNLIMITED_STOCK = -1;

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
	@Size(max = PRODUCT_NAME_LEN)
	@Column(name = "name", length = PRODUCT_NAME_LEN, nullable = false)
	protected String name;

	// product type
	@Enumerated(EnumType.STRING)
	@Column(name = "type", length = PRODUCT_TYPE_LEN, nullable = false)
	protected Type type;

	// product category
	@Enumerated(EnumType.STRING)
	@Column(name = "category", length = PRODUCT_CATEGORY_LEN, nullable = false)
	protected Category category;

	// product image normal size
	@Size(max = IMAGE_URL_LEN)
	@Column(name = "image", length = IMAGE_URL_LEN)
	protected String image;

	// product image small size
	@Size(max = IMAGE_URL_LEN)
	@Column(name = "image_small", length = IMAGE_URL_LEN)
	protected String imageSmall;

	// product stock
	@Min(UNLIMITED_STOCK)
	@Column(name = "stock", nullable = false)
	protected int stock = UNLIMITED_STOCK;

	// product original price
	@Column(name = "original_price")
	protected BigDecimal originalPrice;

	// product special price
	@Column(name = "special_price")
	protected BigDecimal specialPrice;

	// product score
	@Column(name = "score", nullable = false)
	protected double score = 0.0;

	// XXX useful?
	// @Column(name = "discount", nullable = true)
	// protected double discount;

	// total sales count
	@Column(name = "total_sales")
	@JsonView({JsonViews.Admin.class, JsonViews.Tenant.class})
	protected int totalSales = 0;

	// product rank (high rank will considered as hot, sort by rank DESC, 0 will be the lowest rank)
	@Min(LOWEST_RANK)
	@Column(name = "rank")
	@JsonView({JsonViews.Admin.class, JsonViews.Tenant.class})
	protected int rank = DEFAULT_RANK;

	// whether it can be published or not
	// e.g. product put on sale/pull off shelves
	// XXX NOTE: a product is published, it means it's already reviewed with APPROVED
	@Column(name = "published", nullable = false)
	@JsonView({JsonViews.Admin.class, JsonViews.Tenant.class})
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
	@JsonView({JsonViews.Admin.class, JsonViews.Tenant.class})
	protected String clientManagers;

	// product description
	@Lob
	@Column(name = "description")
	protected String description;

	// product price
	@Column(name = "price", nullable = false)
	protected BigDecimal price = BigDecimal.ZERO;

	// product price CurrencyUnit
	@Enumerated(EnumType.STRING)
	@ColumnDefault("'RMB'")
	@Column(name = "currency_unit", nullable = false, length = CURRENCY_UNIT_LEN)
	//@Column(name = "currency_unit", nullable = false, length = CURRENCY_UNIT_LEN, columnDefinition = "varchar(16) default 'RMB'")
	protected CurrencyUnit currencyUnit = CurrencyUnit.RMB;

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public CurrencyUnit getCurrencyUnit() {
		return currencyUnit;
	}

	public void setCurrencyUnit(CurrencyUnit currencyUnit) {
		this.currencyUnit = currencyUnit;
	}

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

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
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

	public String getImageSmall() {
		return imageSmall;
	}

	public void setImageSmall(String imageSmall) {
		this.imageSmall = imageSmall;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public BigDecimal getOriginalPrice() {
		return originalPrice;
	}

	public void setOriginalPrice(BigDecimal originalPrice) {
		this.originalPrice = originalPrice;
	}

	public BigDecimal getSpecialPrice() {
		return specialPrice;
	}

	public void setSpecialPrice(BigDecimal specialPrice) {
		this.specialPrice = specialPrice;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
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

	/**
	 * Return true if this product has stock limit
	 *
	 * @return true if this product has stock limit
	 */
	@XmlTransient
	public boolean hasStockLimit() {
		return stock != UNLIMITED_STOCK;
	}

	/**
	 * Return true if this product stock is available for quantity
	 *
	 * @param quantity the number of requested product quantity to buy
	 * @return true if this product stock available
	 */
	public boolean isStockAvailable(int quantity) {
		// 1) no stock limit
		// 2) quantity is less than or equal to current stock.
		return !hasStockLimit() || (hasStockLimit() && stock >= quantity);
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

	/**
	 * Product type
	 */
	public enum Type {
		FLEET, FERRYFLIGHT, JETTRAVEL, AIRTAXI, AIRTOUR, AIRTRANSPORT, COURSE, QUICKFLIGHT;

		/**
		 * Lower case names in string.
		 */
		public static final Set<String> NAMES = Stream.of(values()).map(e -> e.name().toLowerCase(Locale.ENGLISH))
				.collect(Collectors.toSet());

		public static Type fromString(String value) {
			for (Type e : values()) {
				if (e.name().equalsIgnoreCase(value)) {
					return e;
				}
			}
			return null;
		}
	}

	/**
	 * Product Category
	 */
	public enum Category {
		NONE, POPUP_ADS, AIR_JET, AIR_TOUR, AIR_TAXI, AIR_TRANS, AIR_TRAINING, AIR_QUICKFLIGHT, AIR_VENUE;

		/**
		 * All categories except none which is meaning less
		 */
		public static Set<Category> ALL = Stream.of(values()).filter(category -> category != Category.NONE)
				.collect(Collectors.toSet());

		public static Category fromString(String value) {
			for (Category e : values()) {
				if (e.name().equalsIgnoreCase(value)) {
					return e;
				}
			}
			return null;
		}
	}
}
