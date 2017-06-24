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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import io.micro.annotation.constraint.NotEmpty;
import net.aircommunity.platform.model.domain.Product.Category;
import net.aircommunity.platform.model.jaxb.DateTimeAdapter;

/**
 * Promotion of an product.
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platform_promotion", indexes = {
		@Index(name = "idx_category_creation_date", columnList = "category,creation_date") })
@XmlAccessorType(XmlAccessType.FIELD)
public class Promotion extends Persistable {
	private static final long serialVersionUID = 1L;

	@Column(name = "category", nullable = false)
	@Enumerated(EnumType.STRING)
	private Category category;

	@Size(max = 255)
	@Column(name = "name", length = 255)
	private String name;

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
		builder.append("Promotion [category=").append(category).append(", name=").append(name).append(", creationDate=")
				.append(creationDate).append(", description=").append(description).append(", id=").append(id)
				.append("]");
		return builder.toString();
	}
}
