package net.aircommunity.platform.model.domain;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Payment result info of an order.
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platform_order_payment", indexes = { //
		@Index(name = "idx_method_trade_no", columnList = "trade_no,method", unique = true),
		@Index(name = "idx_method_order_no", columnList = "order_no,method", unique = true),
		@Index(name = "idx_user_id_method", columnList = "user_id,method,timestamp"),
		@Index(name = "idx_tenant_id_method", columnList = "tenant_id,method,timestamp")//
})
@XmlAccessorType(XmlAccessType.FIELD)
public class Payment extends Trade {
	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Payment [id=").append(id).append(", tradeNo=").append(tradeNo).append(", orderNo=")
				.append(orderNo).append(", requestNo=").append(requestNo).append(", amount=").append(amount)
				.append(", method=").append(method).append(", status=").append(status).append(", timestamp=")
				.append(timestamp).append(", note=").append(note).append("]");
		return builder.toString();
	}
}
