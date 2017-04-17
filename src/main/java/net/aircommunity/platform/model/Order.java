package net.aircommunity.platform.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.aircommunity.platform.common.OrderNoGenerator;
import net.aircommunity.platform.model.jaxb.AccountAdapter;

/**
 * Base Order model.
 * 
 * @author Bin.Zhang
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class Order extends Persistable {
	private static final long serialVersionUID = 1L;

	private static final transient OrderNoGenerator ORDERNO_GENERATOR = OrderNoGenerator.INSTANCE;

	// Order Number
	@Column(name = "order_no", nullable = false, unique = true)
	protected Long orderNo;

	@Column(name = "status", nullable = false)
	@Enumerated(EnumType.STRING)
	protected Status status;

	@Column(name = "is_commented", nullable = false)
	protected Boolean commented = Boolean.FALSE;

	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "creation_date", nullable = false)
	protected Date creationDate;

	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "payment_date")
	protected Date paymentDate;

	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "finished_date")
	protected Date finishedDate;

	// customer extra information for an order
	@Lob
	@Column(name = "note")
	protected String note;

	@XmlJavaTypeAdapter(AccountAdapter.class)
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	protected User owner;

	@PrePersist
	private void generateOrderNo() {
		if (orderNo == null) {
			orderNo = ORDERNO_GENERATOR.nextOrderNo();
		}
	}

	public Long getOrderNo() {
		return orderNo;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Boolean getCommented() {
		return commented;
	}

	public void setCommented(Boolean commented) {
		this.commented = commented;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public Date getFinishedDate() {
		return finishedDate;
	}

	public void setFinishedDate(Date finishedDate) {
		this.finishedDate = finishedDate;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	// @XmlTransient
	public abstract Product getProduct();

	/**
	 * Order status
	 */
	public enum Status {
		// TODO check if PUBLISHED needed
		PUBLISHED, PENDING, PAID, FINISHED, CANCELLED,

		/**
		 * Not visible to user or tenant, only available to platform admin
		 */
		DELETED;

		public static Status of(String value) {
			for (Status e : values()) {
				if (e.name().equalsIgnoreCase(value)) {
					return e;
				}
			}
			return null;
		}
	}
}
