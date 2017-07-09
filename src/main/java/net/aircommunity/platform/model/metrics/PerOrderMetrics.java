package net.aircommunity.platform.model.metrics;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Per Order metrics.
 * 
 * @author Bin.Zhang
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class PerOrderMetrics implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	// orders overall count per order type
	private final long totalCount;
	// orders count this month
	private final long countThisMonth;
	// orders count today
	private final long countToday;

	public PerOrderMetrics(long totalCount, long countThisMonth, long countToday) {
		this.totalCount = totalCount;
		this.countThisMonth = countThisMonth;
		this.countToday = countToday;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public long getCountThisMonth() {
		return countThisMonth;
	}

	public long getCountToday() {
		return countToday;
	}

	@Override
	public PerOrderMetrics clone() {
		try {
			return (PerOrderMetrics) super.clone();
		}
		catch (CloneNotSupportedException e) {
			throw new AssertionError(e);
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PerOrderMetrics [totalCount=").append(totalCount).append(", countThisMonth=")
				.append(countThisMonth).append(", countToday=").append(countToday).append("]");
		return builder.toString();
	}

}
