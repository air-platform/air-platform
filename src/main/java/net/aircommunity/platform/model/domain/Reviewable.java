package net.aircommunity.platform.model.domain;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.fasterxml.jackson.annotation.JsonView;

import net.aircommunity.platform.model.JsonViews;

/**
 * Reviewable entity of a TENANT, it should be review by ADMIN before it can be published.
 * 
 * @author Bin.Zhang
 */
@MappedSuperclass
@XmlAccessorType(XmlAccessType.FIELD)
public class Reviewable extends Persistable {
	private static final long serialVersionUID = 1L;

	// whether a product is approved or not by platform ADMIN
	@Column(name = "review_status", length = 10, nullable = false)
	@Enumerated(EnumType.STRING)
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	protected ReviewStatus reviewStatus = ReviewStatus.PENDING;

	// reason when it's rejected by platform ADMIN
	@Size(max = 1024)
	@Column(name = "rejected_reason", length = 1024)
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	protected String rejectedReason;

	public ReviewStatus getReviewStatus() {
		return reviewStatus;
	}

	public void setReviewStatus(ReviewStatus reviewStatus) {
		this.reviewStatus = reviewStatus;
	}

	public String getRejectedReason() {
		return rejectedReason;
	}

	public void setRejectedReason(String rejectedReason) {
		this.rejectedReason = rejectedReason;
	}

	public enum ReviewStatus {
		PENDING, APPROVED, REJECTED;

		public static ReviewStatus fromString(String value) {
			for (ReviewStatus e : values()) {
				if (e.name().equalsIgnoreCase(value)) {
					return e;
				}
			}
			return null;
		}
	}

}
