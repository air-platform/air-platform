package net.aircommunity.platform.model.metrics;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import net.aircommunity.platform.model.domain.Product;

/**
 * Order metrics.
 * 
 * @author Bin.Zhang
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class OrderMetrics implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	// orders overall count
	private long totalCount;
	// orders count this month
	private long countThisMonth;
	// orders count today
	private long countToday;

	// details per order basis
	private final Map<String, PerOrderMetrics> details = new HashMap<>(8);

	public long getTotalCount() {
		return totalCount;
	}

	public long getCountThisMonth() {
		return countThisMonth;
	}

	public long getCountToday() {
		return countToday;
	}

	public PerOrderMetrics getPerOrderMetrics(Product.Type type) {
		return details.get(type.name().toLowerCase(Locale.ENGLISH));
	}

	@Override
	public OrderMetrics clone() {
		try {
			return (OrderMetrics) super.clone();
		}
		catch (CloneNotSupportedException e) {
			throw new AssertionError(e);
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OrderMetrics [totalCount=").append(totalCount).append(", countThisMonth=")
				.append(countThisMonth).append(", countToday=").append(countToday).append(", details=").append(details)
				.append("]");
		return builder.toString();
	}

	/**
	 * Metrics builder.
	 * 
	 * @return builder
	 */
	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private final OrderMetrics metrics;

		private Builder() {
			metrics = new OrderMetrics();
		}

		public Builder setTotalCount(long totalCount) {
			metrics.totalCount = totalCount;
			return this;
		}

		public Builder setCountThisMonth(long countThisMonth) {
			metrics.countThisMonth = countThisMonth;
			return this;
		}

		public Builder setCountToday(long countToday) {
			metrics.countToday = countToday;
			return this;
		}

		public Builder setPerOrderMetrics(Product.Type type, PerOrderMetrics perMetrics) {
			metrics.details.put(type.name().toLowerCase(Locale.ENGLISH), perMetrics);
			return this;
		}

		public OrderMetrics build() {
			return metrics.clone();
		}

		public OrderMetrics buildAndSum() {
			OrderMetrics orderMetrics = metrics.clone();
			orderMetrics.details.values().stream().forEach(metrics -> {
				orderMetrics.totalCount += metrics.getTotalCount();
				orderMetrics.countThisMonth += metrics.getCountThisMonth();
				orderMetrics.countToday += metrics.getCountToday();
			});
			return orderMetrics;
		}
	}
}
