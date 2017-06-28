package net.aircommunity.platform.model.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import io.micro.annotation.constraint.NotEmpty;
import net.aircommunity.platform.model.LinkType;
import net.aircommunity.platform.model.jaxb.DateTimeAdapter;
import net.aircommunity.platform.model.jaxb.PromotionAdapter;

/**
 * Promotion of an product.
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platform_promotion_item")
@XmlAccessorType(XmlAccessType.FIELD)
public class PromotionItem extends Persistable {
	private static final long serialVersionUID = 1L;

	@NotEmpty
	@Size(max = 255)
	@Column(name = "title", nullable = false)
	private String title;

	@Size(max = 255)
	@Column(name = "image")
	private String image;

	// product link
	@Size(max = 1024)
	@Column(name = "link", length = 1024)
	private String link;

	// link type
	@Column(name = "link_type", length = 10, nullable = false)
	@Enumerated(EnumType.STRING)
	private LinkType linkType = LinkType.PRODUCT;

	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "creation_date", nullable = false)
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	private Date creationDate;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "promotion_id", nullable = false)
	@XmlJavaTypeAdapter(PromotionAdapter.class)
	private Promotion promotion;

	public PromotionItem() {
	}

	public PromotionItem(String id) {
		this.id = id;
	}

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

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Promotion getPromotion() {
		return promotion;
	}

	public void setPromotion(Promotion promotion) {
		this.promotion = promotion;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PromotionItem [title=").append(title).append(", image=").append(image).append(", link=")
				.append(link).append(", linkType=").append(linkType).append(", creationDate=").append(creationDate)
				.append(", id=").append(id).append("]");
		return builder.toString();
	}

}
