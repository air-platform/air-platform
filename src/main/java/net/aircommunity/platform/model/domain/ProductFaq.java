package net.aircommunity.platform.model.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
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

import net.aircommunity.platform.model.jaxb.DateTimeAdapter;
import net.aircommunity.platform.model.jaxb.ProductAdapter;

/**
 * FAQ of an product.
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platform_product_faq")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductFaq extends Persistable {
	private static final long serialVersionUID = 1L;

	@Size(max = 255)
	@Column(name = "title", length = 255)
	private String title;

	@Lob
	@Column(name = "content")
	private String content;

	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "date", nullable = false)
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	private Date date;

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

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProductFaq [title=").append(title).append(", content=").append(content).append(", date=")
				.append(date).append(", product=").append(product).append(", id=").append(id).append("]");
		return builder.toString();
	}
}
