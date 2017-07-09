package net.aircommunity.platform.model.metrics;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Per Trade metrics.
 * 
 * @author Bin.Zhang
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class PerTradeMetrics implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	// revenue
	private BigDecimal revenueYearly = BigDecimal.ZERO;
	private BigDecimal revenueQuarterly = BigDecimal.ZERO;
	private BigDecimal revenueMonthly = BigDecimal.ZERO;
	// expense
	private BigDecimal expenseYearly = BigDecimal.ZERO;
	private BigDecimal expenseQuarterly = BigDecimal.ZERO;
	private BigDecimal expenseMonthly = BigDecimal.ZERO;

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

	@Override
	public PerTradeMetrics clone() {
		try {
			return (PerTradeMetrics) super.clone();
		}
		catch (CloneNotSupportedException e) {
			throw new AssertionError(e);
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PerTradeMetrics [revenueYearly=").append(revenueYearly).append(", revenueQuarterly=")
				.append(revenueQuarterly).append(", revenueMonthly=").append(revenueMonthly).append(", expenseYearly=")
				.append(expenseYearly).append(", expenseQuarterly=").append(expenseQuarterly)
				.append(", expenseMonthly=").append(expenseMonthly).append("]");
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
		private final PerTradeMetrics metrics;

		private Builder() {
			metrics = new PerTradeMetrics();
		}

		public Builder setRevenueYearly(BigDecimal revenueYearly) {
			if (revenueYearly != null) {
				metrics.revenueYearly = revenueYearly;
			}
			return this;
		}

		public Builder setRevenueQuarterly(BigDecimal revenueQuarterly) {
			if (revenueQuarterly != null) {
				metrics.revenueQuarterly = revenueQuarterly;
			}
			return this;
		}

		public Builder setRevenueMonthly(BigDecimal revenueMonthly) {
			if (revenueMonthly != null) {
				metrics.revenueMonthly = revenueMonthly;
			}
			return this;
		}

		public Builder setExpenseYearly(BigDecimal expenseYearly) {
			if (expenseYearly != null) {
				metrics.expenseYearly = expenseYearly;
			}
			return this;
		}

		public Builder setExpenseQuarterly(BigDecimal expenseQuarterly) {
			if (expenseQuarterly != null) {
				metrics.expenseQuarterly = expenseQuarterly;
			}
			return this;
		}

		public Builder setExpenseMonthly(BigDecimal expenseMonthly) {
			if (expenseMonthly != null) {
				metrics.expenseMonthly = expenseMonthly;
			}
			return this;
		}

		public PerTradeMetrics build() {
			return metrics.clone();
		}
	}
}
