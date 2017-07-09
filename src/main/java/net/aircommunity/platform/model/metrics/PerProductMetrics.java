package net.aircommunity.platform.model.metrics;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Per Product metrics.
 * 
 * @author Bin.Zhang
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class PerProductMetrics implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	private long totalCount;
	private long publishedCount;
	private long unpublishedCount;
	private long reviewApprovedCount;
	private long reviewPendingCount;
	private long reviewRejectedCount;

	public long getTotalCount() {
		return totalCount;
	}

	public long getPublishedCount() {
		return publishedCount;
	}

	public long getUnpublishedCount() {
		return unpublishedCount;
	}

	public long getReviewPendingCount() {
		return reviewPendingCount;
	}

	public long getReviewRejectedCount() {
		return reviewRejectedCount;
	}

	public long getReviewApprovedCount() {
		return reviewApprovedCount;
	}

	@Override
	public PerProductMetrics clone() {
		try {
			return (PerProductMetrics) super.clone();
		}
		catch (CloneNotSupportedException e) {
			throw new AssertionError(e);
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PerProductMetrics [totalCount=").append(totalCount).append(", publishedCount=")
				.append(publishedCount).append(", unpublishedCount=").append(unpublishedCount)
				.append(", reviewPendingCount=").append(reviewPendingCount).append(", reviewRejectedCount=")
				.append(reviewRejectedCount).append(", reviewApprovedCount=").append(reviewApprovedCount).append("]");
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
		private final PerProductMetrics metrics;

		private Builder() {
			metrics = new PerProductMetrics();
		}

		public Builder setTotalCount(long totalCount) {
			metrics.totalCount = totalCount;
			return this;
		}

		public Builder setPublishedCount(long publishedCount) {
			metrics.publishedCount = publishedCount;
			return this;
		}

		public Builder setUnpublishedCount(long unpublishedCount) {
			metrics.unpublishedCount = unpublishedCount;
			return this;
		}

		public Builder setReviewPendingCount(long reviewPendingCount) {
			metrics.reviewPendingCount = reviewPendingCount;
			return this;
		}

		public Builder setReviewRejectedCount(long reviewRejectedCount) {
			metrics.reviewRejectedCount = reviewRejectedCount;
			return this;
		}

		public Builder setReviewApprovedCount(long reviewApprovedCount) {
			metrics.reviewApprovedCount = reviewApprovedCount;
			return this;
		}

		public PerProductMetrics build() {
			return metrics.clone();
		}
	}
}
