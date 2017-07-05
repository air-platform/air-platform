package net.aircommunity.platform.model.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.aircommunity.platform.model.jaxb.AccountAdapter;
import net.aircommunity.platform.model.jaxb.DateTimeAdapter;

/**
 * Payment result info of an order.
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platform_order_payment", indexes = {
		//
		@Index(name = "idx_method_trade_no", columnList = "trade_no,method", unique = true),
		@Index(name = "idx_method_order_no", columnList = "order_no,method", unique = true),
		@Index(name = "idx_user_id_method", columnList = "user_id,method,timestamp"),
		@Index(name = "idx_tenant_id_method", columnList = "tenant_id,method,timestamp")
		//
})
@XmlAccessorType(XmlAccessType.FIELD)
public class Payment extends Persistable {
	private static final long serialVersionUID = 1L;

	// trade No. of payment system
	@Size(max = TRADE_NO_LEN)
	@Column(name = "trade_no", length = TRADE_NO_LEN, nullable = false)
	private String tradeNo;

	// order no
	@Size(max = ORDER_NO_LEN)
	@Column(name = "order_no", length = ORDER_NO_LEN, nullable = false)
	private String orderNo;

	// amount of payment
	@Column(name = "amount", nullable = false)
	private BigDecimal amount = BigDecimal.ZERO;

	// payment method: alipay, wechat etc.
	@Enumerated(EnumType.STRING)
	@Column(name = "method", length = PAYMENT_METHOD_LEN, nullable = false)
	private Method method;

	@Size(max = DEFAULT_FIELD_LEN)
	@Column(name = "note")
	private String note;

	// XXX NOTE: we only saved success payment ATM, also history for failures?
	// the status of this payment
	// @Column(name = "status", nullable = false)
	// @Enumerated(EnumType.STRING)
	// private Status status;

	@Column(name = "timestamp", nullable = false)
	@Temporal(value = TemporalType.TIMESTAMP)
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	private Date timestamp;

	// who paid (from)
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	@XmlJavaTypeAdapter(AccountAdapter.class)
	private User owner;

	// to which tenant (to)
	@ManyToOne
	@JoinColumn(name = "tenant_id", nullable = false)
	@XmlJavaTypeAdapter(AccountAdapter.class)
	private Tenant vendor;

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

	/**
	 * Payment method
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Payment [id=").append(id).append(", tradeNo=").append(tradeNo).append(", orderNo=")
				.append(orderNo).append(", amount=").append(amount).append(", method=").append(method).append(", note=")
				.append(note).append(", timestamp=").append(timestamp).append("]");
		return builder.toString();
	}

	/**
	 * Payment status (useful?), we only save SUCCESS payment
	 */
	// public enum Status {
	// SUCCESS, FAILURE;
	//
	// public static Status fromString(String value) {
	// for (Status e : values()) {
	// if (e.name().equalsIgnoreCase(value)) {
	// return e;
	// }
	// }
	// return null;
	// }
	// }

}
