package net.aircommunity.platform.model;

import java.util.Date;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonValue;

import net.aircommunity.platform.model.jaxb.AccountAdapter;

/**
 * Base Order model.
 * 
 * @author Bin.Zhang
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@XmlAccessorType(XmlAccessType.NONE)
public abstract class Order extends Persistable {
	private static final long serialVersionUID = 1L;

	// Order Number
	@XmlElement
	@Column(name = "order_no", nullable = false, unique = true)
	protected String orderNo;

	@XmlElement
	@Column(name = "status", nullable = false)
	@Enumerated(EnumType.STRING)
	protected Status status;

	@XmlElement
	@Column(name = "is_commented", nullable = false)
	protected Boolean commented = Boolean.FALSE;

	@XmlElement
	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "creation_date", nullable = false)
	protected Date creationDate;

	@XmlElement
	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "payment_date")
	protected Date paymentDate;

	@XmlElement
	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "finished_date")
	protected Date finishedDate;

	@XmlElement
	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "cancelled_date")
	protected Date cancelledDate;

	@XmlElement
	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "deleted_date")
	protected Date deletedDate;

	// customer extra information for an order
	@XmlElement
	@Lob
	@Column(name = "note")
	protected String note;

	@XmlElement
	@XmlJavaTypeAdapter(AccountAdapter.class)
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	protected User owner;

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
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

	public Date getCancelledDate() {
		return cancelledDate;
	}

	public void setCancelledDate(Date cancelledDate) {
		this.cancelledDate = cancelledDate;
	}

	public Date getDeletedDate() {
		return deletedDate;
	}

	public void setDeletedDate(Date deletedDate) {
		this.deletedDate = deletedDate;
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

	// order type (used by for RESTful API, not persisted)
	@XmlElement(name = "type")
	public String getTypeAsString() {
		return getType().name().toLowerCase(Locale.ENGLISH);
	}

	// Not sure why this enum cannot be serialized to JSON (event with @XmlElement), so just use getTypeString instead
	@XmlTransient
	public abstract Type getType();

	@XmlTransient
	public abstract Product getProduct();

	/**
	 * Order product type
	 */
	public enum Type {
		FLEET, FERRYFLIGHT, JETCARD, AIRTAXI, AIRTOUR, AIRTRANSPORT, COURSE;

		@JsonValue
		public String toString() {
			return super.toString().toLowerCase();
		}
	}

	/**
	 * Order status
	 */
	public enum Status {

		/**
		 * Published
		 */
		PUBLISHED,

		/**
		 * Pending
		 */
		PENDING,

		/**
		 * Paid
		 */
		PAID,

		/**
		 * Finished
		 */
		FINISHED,

		/**
		 * Cancelled
		 */
		CANCELLED,

		/**
		 * Not visible to user or tenant, only available to platform admin
		 */
		DELETED;

		public static Status fromString(String value) {
			for (Status e : values()) {
				if (e.name().equalsIgnoreCase(value)) {
					return e;
				}
			}
			return null;
		}
	}
}
