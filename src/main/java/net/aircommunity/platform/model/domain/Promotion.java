package net.aircommunity.platform.model.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonView;

import io.micro.annotation.constraint.NotEmpty;
import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.domain.Product.Category;
import net.aircommunity.platform.model.jaxb.DateTimeAdapter;

/**
 * Promotion of an product.
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platform_promotion", indexes = {
		@Index(name = "idx_category_rank_creation_date", columnList = "category,rank,creation_date") })
@XmlAccessorType(XmlAccessType.FIELD)
public class Promotion extends Persistable {
	private static final long serialVersionUID = 1L;

	/**
	 * Lowest product rank
	 */
	public static final int LOWEST_RANK = 0;

	/**
	 * Default product rank
	 */
	public static final int DEFAULT_RANK = LOWEST_RANK;

	@NotEmpty
	@Size(max = PRODUCT_NAME_LEN) // same as product name length
	@Column(name = "name", length = PRODUCT_NAME_LEN, nullable = false)
	private String name;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "category", length = PRODUCT_CATEGORY_LEN, nullable = false)
	private Category category;

	// product rank (high rank will considered as hot, sort by rank DESC, 0 will be the lowest rank)
	@Min(LOWEST_RANK)
	@Column(name = "rank", nullable = false)
	@JsonView(JsonViews.Admin.class)
	private int rank = DEFAULT_RANK;

	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "creation_date", nullable = false)
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	private Date creationDate;

	// product description
	@Lob
	@Column(name = "description")
	private String description;

	// order matter
	@NotEmpty
	@OneToMany(mappedBy = "promotion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<PromotionItem> items = new ArrayList<>();

	public Promotion() {
	}

	public Promotion(String id) {
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<PromotionItem> getItems() {
		return items;
	}

	public void setItems(List<PromotionItem> items) {
		if (items != null) {
			items.stream().forEach(item -> {
				item.setPromotion(this);
				item.setCreationDate(new Date());
			});
			this.items.clear();
			this.items.addAll(items);
		}
	}

	public boolean addItem(PromotionItem item) {
		if (items.contains(item)) {
			return false;
		}
		item.setPromotion(this);
		return items.add(item);
	}

	public boolean removeItem(PromotionItem item) {
		if (!items.contains(item)) {
			return false;
		}
		return items.remove(item);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Promotion [category=").append(category).append(", name=").append(name).append(", rank=")
				.append(rank).append(", creationDate=").append(creationDate).append(", description=")
				.append(description).append(", items=").append(items).append(", id=").append(id).append("]");
		return builder.toString();
	}
}
