package net.aircommunity.platform.model.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.aircommunity.platform.model.domain.Product.Type;
import net.aircommunity.platform.model.jaxb.DateTimeAdapter;

/**
 * TODO Order reference for fast lookup. really useful? (all order ref in the same table)
 * 
 * @author Bin.Zhang
 */
// @Entity
// @Table(name = "air_platform_order", indexes = {
// @Index(name = "idx_user_id_status", columnList = "user_id,creation_date")
// //
// })
public class OrderRef implements Serializable {
	private static final long serialVersionUID = 1L;

	// order id
	@Id
	@Column(name = "order_id")
	private String orderId;

	@Column(name = "user_id", nullable = false)
	private String ownerId;

	// order number
	@Column(name = "order_no", nullable = false, unique = true)
	private String orderNo;

	// order type
	@Column(name = "order_type", nullable = false)
	@Enumerated(EnumType.STRING)
	private Type orderType;

	// creation date of order
	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "order_creation_date", nullable = false)
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	private Date orderCreationDate;
}
