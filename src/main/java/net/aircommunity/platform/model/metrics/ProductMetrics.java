package net.aircommunity.platform.model.metrics;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import net.aircommunity.platform.model.domain.Product;

/**
 * Product metrics.
 * 
 * @author Bin.Zhang
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductMetrics implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	// overall
	private long totalCount;
	private long publishedCount;
	private long unpublishedCount;
	private long reviewApprovedCount;
	private long reviewPendingCount;
	private long reviewRejectedCount;

	// school is not product
	private long schoolCount;

	// per product basis
	private final Map<String, PerProductMetrics> details = new HashMap<>(8);

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

	public long getSchoolCount() {
		return schoolCount;
	}

	public PerProductMetrics getPerProductMetrics(Product.Type type) {
		return details.get(type.name().toLowerCase(Locale.ENGLISH));
	}

	@Override
	public ProductMetrics clone() {
		try {
			return (ProductMetrics) super.clone();
		}
		catch (CloneNotSupportedException e) {
			throw new AssertionError(e);
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProductMetrics [totalCount=").append(totalCount).append(", publishedCount=")
				.append(publishedCount).append(", unpublishedCount=").append(unpublishedCount)
				.append(", reviewApprovedCount=").append(reviewApprovedCount).append(", reviewPendingCount=")
				.append(reviewPendingCount).append(", reviewRejectedCount=").append(reviewRejectedCount)
				.append(", schoolCount=").append(schoolCount).append(", details=").append(details).append("]");
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
		private final ProductMetrics metrics;

		private Builder() {
			metrics = new ProductMetrics();
		}

		public Builder setSchoolCount(long schoolCount) {
			metrics.schoolCount = schoolCount;
			return this;
		}

		public Builder setPerProductMetrics(Product.Type type, PerProductMetrics perMetrics) {
			metrics.details.put(type.name().toLowerCase(Locale.ENGLISH), perMetrics);
			return this;
		}

		public ProductMetrics buildAndSum() {
			ProductMetrics productMetrics = metrics.clone();
			productMetrics.details.values().stream().forEach(metrics -> {
				productMetrics.totalCount += metrics.getTotalCount();
				productMetrics.publishedCount += metrics.getPublishedCount();
				productMetrics.unpublishedCount += metrics.getUnpublishedCount();
				productMetrics.reviewApprovedCount += metrics.getReviewApprovedCount();
				productMetrics.reviewPendingCount += metrics.getReviewPendingCount();
				productMetrics.reviewRejectedCount += metrics.getReviewRejectedCount();
			});
			return productMetrics;
		}
	}

}
