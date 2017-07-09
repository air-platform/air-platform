package net.aircommunity.platform.model.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
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

import io.micro.annotation.constraint.NotEmpty;
import net.aircommunity.platform.model.jaxb.DateTimeAdapter;
import net.aircommunity.platform.model.jaxb.ProductAdapter;

/**
 * FAQ of an product.
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platform_product_faq", indexes = {
		@Index(name = "idx_product_id_date", columnList = "product_id,creation_date") //
})
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductFaq extends Persistable {
	private static final long serialVersionUID = 1L;

	@NotEmpty
	@Size(max = PRODUCT_FAQ_TITLE_LEN)
	@Column(name = "title", length = PRODUCT_FAQ_TITLE_LEN, nullable = false)
	private String title;

	@Lob
	@Column(name = "content")
	private String content;

	// the number of views
	@Column(name = "views", nullable = false)
	private int views = 0;

	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "creation_date", nullable = false)
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	private Date creationDate;

	// make comment on a product
	@ManyToOne
	@JoinColumn(name = "product_id", nullable = false)
	@XmlJavaTypeAdapter(ProductAdapter.class)
	private Product product;

	public ProductFaq() {
	}

	public ProductFaq(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void increaseViews() {
		views = views + 1;
	}

	public int getViews() {
		return views;
	}

	public void setViews(int views) {
		this.views = views;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
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
		builder.append("ProductFaq [id=").append(id).append(", title=").append(title).append(", content=")
				.append(content).append(", views=").append(views).append(", creationDate=").append(creationDate)
				.append(", product=").append(product).append("]");
		return builder.toString();
	}
}
