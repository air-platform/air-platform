package net.aircommunity.platform.service.internal;

import java.io.Serializable;
import java.util.Objects;

import javax.annotation.concurrent.Immutable;

import net.aircommunity.platform.model.Order;

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
	// private final Product product;

	public OrderEvent(EventType type, Order order) {
		this.type = type;
		this.order = Objects.requireNonNull(order, "order cannot null");
		// this.product = product;
	}

	public EventType getType() {
		return type;
	}

	public Order getOrder() {
		return order;
	}

	// public Product getProduct() {
	// return product;
	// }

	public enum EventType {
		CREATION, UPDATE, CANCELLATION, DELETION;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OrderEvent [type=").append(type).append(", order=").append(order).append("]");
		return builder.toString();
	}

}
