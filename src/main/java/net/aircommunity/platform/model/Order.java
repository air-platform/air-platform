package net.aircommunity.platform.model;

import java.util.Date;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Embedded;
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
import com.fasterxml.jackson.annotation.JsonView;

import net.aircommunity.platform.model.jaxb.AccountAdapter;
import net.aircommunity.platform.model.jaxb.DateTimeAdapter;

/**
 * Base Order model. TODO have multiple paymentInfo? 分期支付
 * 
 * @author Bin.Zhang
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@XmlAccessorType(XmlAccessType.NONE)
public abstract class Order extends Persistable {
	private static final long serialVersionUID = 1L;

	// TODO add alipay TradeNo?
	// @XmlElement --> product
	// @Column(name = "discount", nullable = true)
	// protected double discount;

	// Order Number
	@XmlElement
	@Column(name = "order_no", nullable = false, unique = true)
	protected String orderNo;

	// points used in this order
	@XmlElement
	@Column(name = "points_used")
	protected int pointsUsed;

	@XmlElement
	@Column(name = "quantity", nullable = false)
	protected int quantity = 1;

	// Order total price
	@XmlElement
	@Column(name = "total_price", nullable = false)
	protected double totalPrice;

	// TODO add nextStatus?
	@XmlElement
	@Column(name = "status", nullable = false)
	@Enumerated(EnumType.STRING)
	protected Status status;

	// only allow comment once per order
	@XmlElement
	@Column(name = "is_commented", nullable = false)
	protected Boolean commented = Boolean.FALSE;

	@XmlElement
	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "creation_date", nullable = false)
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	protected Date creationDate;

	@XmlElement
	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "last_modified_date", nullable = false)
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	protected Date lastModifiedDate;

	@XmlElement
	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "payment_date")
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	protected Date paymentDate;

	@XmlElement
	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "finished_date")
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	protected Date finishedDate;

	@XmlElement
	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "cancelled_date")
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	protected Date cancelledDate;

	@XmlElement
	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "deleted_date")
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@JsonView(JsonViews.Admin.class)
	protected Date deletedDate;

	@XmlElement
	@Embedded
	protected PaymentInfo paymentInfo;

	// customer contact information for this order
	@Embedded
	protected Contact contact;

	// information to customer if the order cannot be accepted, reject reason? TODO
	@XmlElement
	@Lob
	@Column(name = "closed_reason")
	protected String closedReason;

	// customer extra information for an order
	@XmlElement
	@Lob
	@Column(name = "note")
	protected String note;

	@XmlElement
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	@XmlJavaTypeAdapter(AccountAdapter.class)
	protected User owner;

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public int getPointsUsed() {
		return pointsUsed;
	}

	public void setPointsUsed(int pointsUsed) {
		this.pointsUsed = pointsUsed;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
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

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
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

	public PaymentInfo getPaymentInfo() {
		return paymentInfo;
	}

	public void setPaymentInfo(PaymentInfo paymentInfo) {
		this.paymentInfo = paymentInfo;
	}

	public String getClosedReason() {
		return closedReason;
	}

	public void setClosedReason(String closedReason) {
		this.closedReason = closedReason;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
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

	public abstract void setProduct(Product product);

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
		 * Published (for airjet published without any aircraft info)
		 */
		PUBLISHED,

		/**
		 * Created
		 */
		CREATED,

		/**
		 * Confirmation
		 */
		PENDING, CONFIRMED,

		/**
		 * Contract (for airjet)
		 */
		CONTRACT_PENDING, CONTRACT_SIGNED,

		/**
		 * Payment
		 */
		TO_BE_PAID, PAID,

		/**
		 * Ticketing
		 */
		TICKETING, TICKET_RELEASED,

		/**
		 * Waiting to travel
		 */
		WAITING_TO_TRAVEL,

		/**
		 * Refund
		 */
		REFUNDING, REFUNDED,

		/**
		 * Finished
		 */
		FINISHED,

		/**
		 * Close due to timeout or close by admin or tenant
		 */
		CLOSED,

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
