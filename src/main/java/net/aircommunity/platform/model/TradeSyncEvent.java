package net.aircommunity.platform.model;

import java.io.Serializable;
import java.util.Objects;

import javax.annotation.concurrent.Immutable;

import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.model.payment.TradeQueryResult;

/**
 * Trade Sync Event.
 * 
 * @author Bin.Zhang
 */
@Immutable
public class TradeSyncEvent implements Serializable {
	private static final long serialVersionUID = 1L;

	private final TradeQueryResult result;
	private final Order order;

	public TradeSyncEvent(TradeQueryResult result, Order order) {
		this.result = Objects.requireNonNull(result, "result cannot null");
		this.order = Objects.requireNonNull(order, "order cannot null");
	}

	public TradeQueryResult getQueryResult() {
		return result;
	}

	public Order getOrder() {
		return order;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TradeSyncEvent [result=").append(result).append(", order=").append(order).append("]");
		return builder.toString();
	}
}
