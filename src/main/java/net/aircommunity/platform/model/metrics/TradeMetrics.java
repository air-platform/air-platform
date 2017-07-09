package net.aircommunity.platform.model.metrics;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import net.aircommunity.platform.model.domain.Trade;

/**
 * Trade metrics.
 * 
 * @author Bin.Zhang
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class TradeMetrics implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	// revenue
	private BigDecimal revenueYearly = BigDecimal.ZERO;
	private BigDecimal revenueQuarterly = BigDecimal.ZERO;
	private BigDecimal revenueMonthly = BigDecimal.ZERO;
	// expense
	private BigDecimal expenseYearly = BigDecimal.ZERO;
	private BigDecimal expenseQuarterly = BigDecimal.ZERO;
	private BigDecimal expenseMonthly = BigDecimal.ZERO;

	// details per trade method basis
	private final Map<String, PerTradeMetrics> details = new HashMap<>(4);

	public BigDecimal getRevenueYearly() {
		return revenueYearly;
	}

	public BigDecimal getRevenueQuarterly() {
		return revenueQuarterly;
	}

	public BigDecimal getRevenueMonthly() {
		return revenueMonthly;
	}

	public BigDecimal getExpenseYearly() {
		return expenseYearly;
	}

	public BigDecimal getExpenseQuarterly() {
		return expenseQuarterly;
	}

	public BigDecimal getExpenseMonthly() {
		return expenseMonthly;
	}

	public PerTradeMetrics getPerTradeMetrics(Trade.Method method) {
		return details.get(method.name().toLowerCase(Locale.ENGLISH));
	}

	@Override
	public TradeMetrics clone() {
		try {
			return (TradeMetrics) super.clone();
		}
		catch (CloneNotSupportedException e) {
			throw new AssertionError(e);
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TradeMetrics [revenueYearly=").append(revenueYearly).append(", revenueQuarterly=")
				.append(revenueQuarterly).append(", revenueMonthly=").append(revenueMonthly).append(", expenseYearly=")
				.append(expenseYearly).append(", expenseQuarterly=").append(expenseQuarterly)
				.append(", expenseMonthly=").append(expenseMonthly).append(", details=").append(details).append("]");
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
		private final TradeMetrics metrics;

		private Builder() {
			metrics = new TradeMetrics();
		}

		public Builder setPerTradeMetrics(Trade.Method method, PerTradeMetrics perMetrics) {
			metrics.details.put(method.name().toLowerCase(Locale.ENGLISH), perMetrics);
			return this;
		}

		public TradeMetrics buildAndSum() {
			TradeMetrics orderMetrics = metrics.clone();
			orderMetrics.details.values().stream().forEach(metrics -> {
				orderMetrics.revenueMonthly = orderMetrics.revenueMonthly.add(metrics.getRevenueMonthly());
				orderMetrics.revenueQuarterly = orderMetrics.revenueQuarterly.add(metrics.getRevenueQuarterly());
				orderMetrics.revenueYearly = orderMetrics.revenueYearly.add(metrics.getRevenueYearly());
				orderMetrics.expenseMonthly = orderMetrics.expenseMonthly.add(metrics.getExpenseMonthly());
				orderMetrics.expenseQuarterly = orderMetrics.expenseQuarterly.add(metrics.getExpenseQuarterly());
				orderMetrics.expenseYearly = orderMetrics.expenseYearly.add(metrics.getExpenseYearly());
			});
			return orderMetrics;
		}
	}

}
