package net.aircommunity.platform.model.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.aircommunity.platform.model.jaxb.AccountAdapter;
import net.aircommunity.platform.model.jaxb.DateTimeAdapter;

/**
 * Trade(payment or refund) of an order.
 * 
 * @author Bin.Zhang
 */
@MappedSuperclass
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class Trade extends Persistable {
	private static final long serialVersionUID = 1L;

	// trade No. of payment system
	@NotNull
	@Size(max = TRADE_NO_LEN)
	@Column(name = "trade_no", length = TRADE_NO_LEN, nullable = false)
	protected String tradeNo;

	@NotNull
	@Size(max = ORDER_NO_LEN)
	@Column(name = "order_no", length = ORDER_NO_LEN, nullable = false)
	protected String orderNo;

	// request no of this trade generated by our platform (optional)
	// 1) payment: used in WECHAT
	// 2) refund: all used
	@Size(max = ORDER_REQUEST_NO_LEN)
	@Column(name = "request_no", length = ORDER_REQUEST_NO_LEN)
	protected String requestNo;

	// amount of payment or refund
	@NotNull
	@Column(name = "amount", nullable = false)
	protected BigDecimal amount = BigDecimal.ZERO;

	// payment method: alipay, wechat etc.
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "method", length = PAYMENT_METHOD_LEN, nullable = false)
	protected Method method;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "status", length = TRADE_STATUS_LEN, nullable = false)
	protected Status status = Status.REQUESTED;

	@Column(name = "timestamp", nullable = false)
	@Temporal(value = TemporalType.TIMESTAMP)
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	protected Date timestamp;

	@Size(max = DEFAULT_FIELD_LEN)
	@Column(name = "note")
	protected String note;

	// who paid (from)
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	@XmlJavaTypeAdapter(AccountAdapter.class)
	protected User owner;

	// to which tenant (to)
	@ManyToOne
	@JoinColumn(name = "tenant_id", nullable = false)
	@XmlJavaTypeAdapter(AccountAdapter.class)
	protected Tenant vendor;

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getRequestNo() {
		return requestNo;
	}

	public void setRequestNo(String requestNo) {
		this.requestNo = requestNo;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
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

	public Tenant getVendor() {
		return vendor;
	}

	public void setVendor(Tenant vendor) {
		this.vendor = vendor;
	}

	public void update(Trade another) {
		if (another != null) {
			tradeNo = another.tradeNo;
			orderNo = another.orderNo;
			requestNo = another.requestNo;
			amount = another.amount;
			method = another.method;
			status = another.status;
			timestamp = another.timestamp;
			note = another.note;
		}
	}

	/**
	 * Trade method
	 */
	public enum Method {

		/**
		 * Alipay
		 */
		ALIPAY,

		/**
		 * Wxpay
		 */
		WECHAT,

		/**
		 * HNA Newpay
		 */
		NEWPAY;

		public static Method fromString(String value) {
			for (Method e : values()) {
				if (e.name().equalsIgnoreCase(value)) {
					return e;
				}
			}
			return null;
		}
	}

	/**
	 * Trade type
	 */
	public enum Type {
		PAYMENT, REFUND;
	}

	/**
	 * Trade status
	 */
	public enum Status {

		/**
		 * Requested
		 */
		REQUESTED,

		/**
		 * In Processing
		 */
		PENDING,

		/**
		 * Success
		 */
		SUCCESS,

		/**
		 * Failure
		 */
		FAILURE;
	}
}