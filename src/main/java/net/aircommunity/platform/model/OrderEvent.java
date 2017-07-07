package net.aircommunity.platform.model;

import java.io.Serializable;
import java.util.Objects;

import javax.annotation.concurrent.Immutable;

import net.aircommunity.platform.model.domain.Order;

/**
 * Order CURD Event.
 * 
 * @author Bin.Zhang
 */
@Immutable
public class OrderEvent implements Serializable {
	private static final long serialVersionUID = 1L;

	private final EventType type;
	private final Order order;

	public OrderEvent(EventType type, Order order) {
		this.type = Objects.requireNonNull(type, "type cannot null");
		this.order = Objects.requireNonNull(order, "order cannot null");
	}

	public EventType getType() {
		return type;
	}

	public Order getOrder() {
		return order;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OrderEvent [type=").append(type).append(", order=").append(order).append("]");
		return builder.toString();
	}

	public enum EventType {
		CREATION, FLEET_OFFERED, UPDATE, PAYMENT, REFUND_REQUESTED, REFUNDED, REFUND_FAILED, CANCELLATION, DELETION;
	}

}
