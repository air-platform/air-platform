package net.aircommunity.platform.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.aircommunity.platform.model.jaxb.AccountAdapter;
import net.aircommunity.platform.model.jaxb.ProductAdapter;

/**
 * Comment of an product.
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platfrom_product_comment")
public class Comment extends Persistable {
	private static final long serialVersionUID = 1L;

	// 0 - 5
	@Min(0)
	@Max(5)
	private int rate;

	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "date", nullable = false)
	private Date date;

	@Lob
	@Column(name = "content")
	private String content;

	// make comment on a product
	@XmlJavaTypeAdapter(ProductAdapter.class)
	@Column(name = "product_id", nullable = false)
	private Product product;

	// owner
	@XmlJavaTypeAdapter(AccountAdapter.class)
	@ManyToOne
	@JoinColumn(name = "account_id", nullable = false)
	private Account owner;

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
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

	public Account getOwner() {
		return owner;
	}

	public void setOwner(Account owner) {
		this.owner = owner;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Comment [rate=").append(rate).append(", date=").append(date).append(", content=")
				.append(content).append(", id=").append(id).append("]");
		return builder.toString();
	}

}
