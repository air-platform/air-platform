package net.aircommunity.platform.model.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.aircommunity.platform.model.domain.Order.Status;
import net.aircommunity.platform.model.domain.Product.Type;
import net.aircommunity.platform.model.jaxb.DateTimeAdapter;

/**
 * Order reference for fast lookup. really useful? (all order reference in a same table), ONLY status is mutable.
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platform_order", indexes = { //
		@Index(name = "idx_type", columnList = "type,creation_date"), // for ADMIN
		@Index(name = "idx_user_id_status", columnList = "user_id,status,creation_date") // for USER
})
public class OrderRef implements DomainConstants, Serializable {
	private static final long serialVersionUID = 1L;

	// order id (UUID)
	@Id
	@Column(name = "order_id", length = ID_LEN)
	private String orderId;

	@Column(name = "user_id", length = ID_LEN, nullable = false)
	private String ownerId;

	// order number
	@Column(name = "order_no", length = ORDER_NO_LEN, nullable = false, unique = true)
	private String orderNo;

	// order status
	@Enumerated(EnumType.STRING)
	@Column(name = "status", length = ORDER_STATUS_LEN, nullable = false)
	private Status status;

	// order payment method (the method is null, if this order is never attempted to pay)
	@Enumerated(EnumType.STRING)
	@Column(name = "payment_method", length = PAYMENT_METHOD_LEN)
	private Trade.Method method;

	// order type = product type
	@Enumerated(EnumType.STRING)
	@Column(name = "type", length = ORDER_TYPE_LEN, nullable = false)
	private Type type;

	// creation date of order
	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "creation_date", nullable = false)
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	private Date creationDate;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

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

	public Trade.Method getMethod() {
		return method;
	}

	public void setMethod(Trade.Method method) {
		this.method = method;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OrderRef [orderId=").append(orderId).append(", ownerId=").append(ownerId).append(", orderNo=")
				.append(orderNo).append(", status=").append(status).append(", method=").append(method).append(", type=")
				.append(type).append(", creationDate=").append(creationDate).append("]");
		return builder.toString();
	}
}
