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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.aircommunity.platform.model.domain.Payment.Method;
import net.aircommunity.platform.model.jaxb.AccountAdapter;
import net.aircommunity.platform.model.jaxb.DateTimeAdapter;

/**
 * Refund result info of an order.
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platfrom_order_refund", indexes = {
		@Index(name = "idx_method_trade_no", columnList = "method,trade_no", unique = true),
		@Index(name = "idx_method_order_no", columnList = "method,order_no", unique = true),
		@Index(name = "idx_user_order", columnList = "user_id,order_no"),
		@Index(name = "idx_tenant_order", columnList = "tenant_id,order_no") })
@XmlAccessorType(XmlAccessType.FIELD)
public class Refund extends Persistable {
	private static final long serialVersionUID = 1L;

	// trade No. of payment system
	@Column(name = "trade_no", nullable = false)
	private String tradeNo;

	// alipay, wechat etc.
	@Column(name = "order_no", nullable = false)
	private String orderNo;

	@Column(name = "refund_reason", nullable = false)
	private String refundReason;

	// refund result note
	@Column(name = "refund_result", nullable = false)
	private String refundResult;

	// amount of payment
	@Column(name = "amount", nullable = false)
	private BigDecimal amount = BigDecimal.ZERO;

	// payment method: alipay, wechat etc.
	@Column(name = "method", nullable = false)
	@Enumerated(EnumType.STRING)
	private Method method;

	@Column(name = "refund_timestamp", nullable = false)
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

	public String getRefundReason() {
		return refundReason;
	}

	public void setRefundReason(String refundReason) {
		this.refundReason = refundReason;
	}

	public String getRefundResult() {
		return refundResult;
	}

	public void setRefundResult(String refundResult) {
		this.refundResult = refundResult;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Refund [tradeNo=").append(tradeNo).append(", orderNo=").append(orderNo)
				.append(", refundReason=").append(refundReason).append(", refundResult=").append(refundResult)
				.append(", amount=").append(amount).append(", method=").append(method).append(", timestamp=")
				.append(timestamp).append(", id=").append(id).append("]");
		return builder.toString();
	}
}
