package net.aircommunity.platform.model.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.aircommunity.platform.model.LinkType;
import net.aircommunity.platform.model.domain.Product.Category;
import net.aircommunity.platform.model.jaxb.DateTimeAdapter;

/**
 * Banner model for Ad.
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platform_banner", indexes = { @Index(name = "idx_category", columnList = "category") })
@XmlAccessorType(XmlAccessType.FIELD)
public class Banner extends Persistable {
	private static final long serialVersionUID = 1L;

	@Size(max = 255)
	@Column(name = "title", length = 255)
	private String title;

	@Size(max = 255)
	@Column(name = "image")
	private String image;

	// product link or html page link etc.
	@Size(max = 1024)
	@Column(name = "link", length = 1024)
	private String link;

	// link type
	@Column(name = "link_type", nullable = false)
	@Enumerated(EnumType.STRING)
	private LinkType linkType = LinkType.PRODUCT;

	// the banner link is which category (should NEVER be NONE)
	@Column(name = "link_category", nullable = false)
	@Enumerated(EnumType.STRING)
	private Category linkCategory = Category.NONE;

	// the banner link is which category
	@Column(name = "category", nullable = false)
	@Enumerated(EnumType.STRING)
	private Category category = Category.NONE;

	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "creation_date", nullable = false)
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	private Date creationDate;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public LinkType getLinkType() {
		return linkType;
	}

	public void setLinkType(LinkType linkType) {
		this.linkType = linkType;
	}

	public Category getLinkCategory() {
		return linkCategory;
	}

	public void setLinkCategory(Category linkCategory) {
		this.linkCategory = linkCategory;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Banner [title=").append(title).append(", image=").append(image).append(", link=").append(link)
				.append(", linkType=").append(linkType).append(", linkCategory=").append(linkCategory)
				.append(", category=").append(category).append(", creationDate=").append(creationDate).append(", id=")
				.append(id).append("]");
		return builder.toString();
	}

}
