package net.aircommunity.platform.model.metrics;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * User Order metrics.
 * 
 * @author Bin.Zhang
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class UserOrderMetrics implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	private final long totalCount;
	private final long finishedCount;
	private final long pendingCount;
	private final long cancelledCount;
	private final long refundCount;

	public UserOrderMetrics(long totalCount, long finishedCount, long pendingCount, long cancelledCount,
			long refundCount) {
		this.totalCount = totalCount;
		this.finishedCount = finishedCount;
		this.pendingCount = pendingCount;
		this.cancelledCount = cancelledCount;
		this.refundCount = refundCount;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public long getFinishedCount() {
		return finishedCount;
	}

	public long getPendingCount() {
		return pendingCount;
	}

	public long getCancelledCount() {
		return cancelledCount;
	}

	public long getRefundCount() {
		return refundCount;
	}

	@Override
	public UserOrderMetrics clone() {
		try {
			return (UserOrderMetrics) super.clone();
		}
		catch (CloneNotSupportedException e) {
			throw new AssertionError(e);
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UserOrderMetrics [totalCount=").append(totalCount).append(", finishedCount=")
				.append(finishedCount).append(", pendingCount=").append(pendingCount).append(", cancelledCount=")
				.append(cancelledCount).append(", refundCount=").append(refundCount).append("]");
		return builder.toString();
	}

}
