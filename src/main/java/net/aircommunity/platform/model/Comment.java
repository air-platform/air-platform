package net.aircommunity.platform.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.aircommunity.platform.model.jaxb.CommenterAdapter;
import net.aircommunity.platform.model.jaxb.DateTimeAdapter;
import net.aircommunity.platform.model.jaxb.ProductAdapter;

/**
 * Comment of an product.
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platfrom_product_comment")
@XmlAccessorType(XmlAccessType.FIELD)
public class Comment extends Persistable {
	private static final long serialVersionUID = 1L;

	// user can only rate once per product per order
	// 0 - 5
	@Min(0)
	@Max(5)
	private int rate;

	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "date", nullable = false)
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	private Date date;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "source")
	private Source source = Source.USER;

	@Lob
	@Column(name = "content")
	private String content;

	// make comment on a product
	@ManyToOne
	@JoinColumn(name = "product_id", nullable = false)
	@XmlJavaTypeAdapter(ProductAdapter.class)
	private Product product;

	// owner
	@ManyToOne
	@JoinColumn(name = "owner_id", nullable = false)
	@XmlJavaTypeAdapter(CommenterAdapter.class)
	private Account owner;

	// reply to
	@ManyToOne
	@JoinColumn(name = "reply_to_id", nullable = true)
	@XmlJavaTypeAdapter(CommenterAdapter.class)
	private Account replyTo;

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

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
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

	public Account getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(Account replyTo) {
		this.replyTo = replyTo;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Comment [rate=").append(rate).append(", date=").append(date).append(", source=").append(source)
				.append(", content=").append(content).append(", replyTo=").append(replyTo).append(", id=").append(id)
				.append("]");
		return builder.toString();
	}

	/**
	 * Comment from which source
	 */
	public enum Source {

		/**
		 * Product Buyer
		 */
		BUYER,

		/**
		 * Normal user
		 */
		USER;

		// According to JSR 311 spec, if used in @QueryParam, fromString is a naming conversion
		public static Source fromString(String source) {
			for (Source e : values()) {
				if (e.name().equalsIgnoreCase(source)) {
					return e;
				}
			}
			return null;
		}
	}

}
