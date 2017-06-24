package net.aircommunity.platform.model.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
import javax.validation.constraints.Min;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonView;

import net.aircommunity.platform.model.CurrencyUnit;
import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.UnitProductPrice;
import net.aircommunity.platform.model.jaxb.AccountAdapter;
import net.aircommunity.platform.model.jaxb.DateTimeAdapter;

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

	/**
	 * All not finished or closed or refund
	 */
	public static final EnumSet<Status> PENDING_STATUSES = EnumSet.of(Status.PUBLISHED, Status.CREATED,
			Status.CUSTOMER_CONFIRMED, Status.CONFIRMED, Status.CONTRACT_SIGNED, Status.TICKET_RELEASED,
			Status.PAYMENT_IN_PROCESS, Status.PARTIAL_PAID, Status.PAID, Status.PAYMENT_FAILED);

	/**
	 * Refund
	 */
	public static final EnumSet<Status> REFUND_STATUSES = EnumSet.of(Status.REFUND_REQUESTED, Status.REFUNDING,
			Status.REFUNDED, Status.REFUND_FAILED);

	/**
	 * Finished or closed
	 */
	public static final EnumSet<Status> COMPLETED_STATUSES = EnumSet.of(Status.FINISHED, Status.CLOSED);

	// allow cancel
	private static final EnumSet<Status> CANCELLABLE_STATUSES = EnumSet.of(Status.PUBLISHED, Status.CREATED,
			Status.CONFIRMED, Status.CONTRACT_SIGNED);

	// finished, closed, cancelled, payment failed or refunded, FIXME: Status.PAYMENT_FAILED also termination??
	private static final EnumSet<Status> TERMINATION_STATUSES = EnumSet.of(Status.FINISHED, Status.CANCELLED,
			Status.PAYMENT_FAILED, Status.REFUNDED, Status.CLOSED);

	// Order Number
	@XmlElement
	@Column(name = "order_no", nullable = false, unique = true)
	protected String orderNo;

	// Order type
	@XmlElement
	@Column(name = "type", nullable = false)
	@Enumerated(EnumType.STRING)
	protected Type type;

	// points used in this order
	@XmlElement
	@Column(name = "points_used")
	protected long pointsUsed;

	@XmlElement
	@Min(1)
	@Column(name = "quantity", nullable = false)
	protected int quantity = 1;

	// Order total price --> total amount
	@XmlElement
	@Column(name = "total_price", nullable = false)
	protected BigDecimal totalPrice = BigDecimal.ZERO;

	// Order original total price
	@XmlElement
	@Column(name = "original_total_price", nullable = false)
	protected BigDecimal originalTotalPrice = BigDecimal.ZERO;

	// Order price CurrencyUnit
	@Column(name = "currency_unit", nullable = false)
	@Enumerated(EnumType.STRING)
	protected CurrencyUnit currencyUnit = CurrencyUnit.RMB;

	// TODO add tradeStatus? PENDING/IN_PROGRESS/FINISHED/CLOSED?
	@XmlElement
	@Column(name = "status", nullable = false)
	@Enumerated(EnumType.STRING)
	protected Status status;

	// order is placed from which channel (Android, iPhone, PC, etc.)
	@XmlElement
	@Column(name = "channel")
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	protected String channel;

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
	@Column(name = "refunded_date")
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	protected Date refundedDate;

	@XmlElement
	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "finished_date")
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	protected Date finishedDate;

	@XmlElement
	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "closed_date")
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@JsonView(JsonViews.Admin.class)
	protected Date closedDate;

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

	// customer contact information for this order
	@Embedded
	protected Contact contact;

	// information from customer if the order should refund
	@XmlElement
	@Lob
	@Column(name = "refund_reason")
	protected String refundReason;

	// information from customer if the order refund failure cause
	@XmlElement
	@Lob
	@Column(name = "refund_failure_cause")
	protected String refundFailureCause;

	// information from customer about cancel
	@XmlElement
	@Lob
	@Column(name = "cancel_reason")
	protected String cancelReason;

	// information to customer if the order cannot be accepted, reject reason
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

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public long getPointsUsed() {
		return pointsUsed;
	}

	public void setPointsUsed(long pointsUsed) {
		this.pointsUsed = pointsUsed;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public BigDecimal getOriginalTotalPrice() {
		return originalTotalPrice;
	}

	public void setOriginalTotalPrice(BigDecimal originalTotalPrice) {
		this.originalTotalPrice = originalTotalPrice;
	}

	public CurrencyUnit getCurrencyUnit() {
		return currencyUnit;
	}

	public void setCurrencyUnit(CurrencyUnit currencyUnit) {
		this.currencyUnit = currencyUnit;
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

	public Date getRefundedDate() {
		return refundedDate;
	}

	public void setRefundedDate(Date refundedDate) {
		this.refundedDate = refundedDate;
	}

	public Date getFinishedDate() {
		return finishedDate;
	}

	public void setFinishedDate(Date finishedDate) {
		this.finishedDate = finishedDate;
	}

	public Date getClosedDate() {
		return closedDate;
	}

	public void setClosedDate(Date closedDate) {
		this.closedDate = closedDate;
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

	public String getRefundReason() {
		return refundReason;
	}

	public void setRefundReason(String refundReason) {
		this.refundReason = refundReason;
	}

	public String getRefundFailureCause() {
		return refundFailureCause;
	}

	public void setRefundFailureCause(String refundFailureCause) {
		this.refundFailureCause = refundFailureCause;
	}

	public String getCancelReason() {
		return cancelReason;
	}

	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
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

	// XXX
	// order type (used by for RESTful API, not persisted)
	// @XmlElement(name = "type")
	// public String getTypeAsString() {
	// return getType().name().toLowerCase(Locale.ENGLISH);
	// }

	// Not sure why this enum cannot be serialized to JSON (event with @XmlElement), so just use getTypeString instead
	// @XmlTransient
	// public abstract Type getType();

	public boolean confirmationRequired() {
		return getType() == Type.FLEET || getType() == Type.JETTRAVEL;
	}

	public boolean signContractRequired() {
		return getType() == Type.FLEET || getType() == Type.COURSE || getType() == Type.FERRYFLIGHT
				|| getType() == Type.JETTRAVEL;
	}

	@XmlTransient
	public boolean isInitialStatus() {
		return status == Order.Status.CREATED || status == Order.Status.PUBLISHED;
	}

	/**
	 * It's ready to start a payment request.
	 */
	@XmlTransient
	public boolean isPayable() {
		return getProduct() != null && (status.ordinal() < Status.CONTRACT_SIGNED.ordinal()
				|| status == Status.PARTIAL_PAID || status == Status.PAYMENT_FAILED);
	}

	/**
	 * Return true if the current order is ready to update to PAID. Order is updated to PAYMENT_IN_PROCESS after payment
	 * is submitted, and this method is called when payment finished notification is received from 3rd payment gateway.
	 * 
	 * @return true if its ready to PAID
	 */
	@XmlTransient
	public boolean canFinishPayment() {
		return getProduct() != null && status.ordinal() < Status.PAID.ordinal();
		// return getProduct() != null && (status.ordinal() < Status.CONTRACT_SIGNED.ordinal()
		// || status == Status.PARTIAL_PAID || status == Status.PAYMENT_FAILED);
	}

	@XmlTransient
	public boolean isCancellable() {
		return CANCELLABLE_STATUSES.contains(status);
	}

	@XmlTransient
	public boolean isCloseable() {
		// same as isCancellable
		return isCancellable();
	}

	/**
	 * Finished, closed, cancelled or refunded.
	 */
	@XmlTransient
	public boolean isCompleted() {
		return TERMINATION_STATUSES.contains(status);
	}

	/**
	 * Returns the sync order status to client without waiting for the final status, it probably paid because of async
	 * notification from payment system that will update the order status to PAID if succeeded, but it still a chance it
	 * may fail. So it is a guess of the current order status for client.
	 * 
	 * @return the true if it's probably paid, otherwise, false for sure if it's NOT paid
	 */
	@XmlTransient
	public boolean isProbablyPaid() {
		return status.ordinal() <= Status.PAID.ordinal();
	}

	/**
	 * Order is completed successfully
	 */
	@XmlTransient
	public boolean isFinished() {
		return status == Status.FINISHED;
	}

	@XmlTransient
	@Nullable
	public abstract Product getProduct();

	public abstract void setProduct(Product product);

	@XmlTransient
	@Nonnull
	public abstract UnitProductPrice getUnitProductPrice();

	/**
	 * Order product type
	 */
	public enum Type {
		FLEET, FERRYFLIGHT, JETTRAVEL, AIRTAXI, AIRTOUR, AIRTRANSPORT, COURSE;

		public static Type fromString(String value) {
			for (Type e : values()) {
				if (e.name().equalsIgnoreCase(value)) {
					return e;
				}
			}
			return null;
		}

		// @JsonValue
		// public String toString() {
		// return super.toString().toLowerCase();
		// }
	}

	/**
	 * Order status, (NOTE: the status order matters before status PAID)
	 */
	public enum Status {

		/**
		 * Published (for airjet published without any aircraft info).
		 * 
		 * XXX NOT USED FOR NOW
		 */
		PUBLISHED,

		/**
		 * Initial state (Creation)
		 */
		CREATED,

		/**
		 * Customer confirmed (e.g. AIRJET fleet selected)
		 */
		CUSTOMER_CONFIRMED,

		/**
		 * Vendor Confirmed
		 */
		// XXX CONFIRM_PENDING status: for AIRJET user need to select a flightCandidate, if allow user to select/cancel
		// flightCandidate. We only allow select once, don't allow cancel, so only one state is needed.
		CONFIRMED,

		/**
		 * Contract (for AIRJET & TRAINING )
		 */
		CONTRACT_SIGNED,

		/**
		 * Payment
		 */
		PAYMENT_IN_PROCESS, PARTIAL_PAID, PAYMENT_FAILED, PAID,

		/**
		 * Ticketing
		 */
		TICKET_RELEASED,

		/**
		 * Refund
		 */
		// user request refund -> tenant accept request -> success
		REFUND_REQUESTED, REFUNDING, REFUND_FAILED, REFUNDED,

		/**
		 * Finished with success
		 */
		FINISHED,

		/**
		 * Closed due to timeout or close by ADMIN or TENANT
		 */
		CLOSED,

		/**
		 * Cancelled by USER, ADMIN or TENANT
		 */
		CANCELLED,

		/**
		 * Not visible to USER and TENANT, only available to platform ADMIN
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

		/**
		 * All status except DELETED.
		 */
		public static Set<Status> visibleStatus() {
			return Stream.of(values()).filter(status -> status != Status.DELETED).collect(Collectors.toSet());
		}
	}
}
