package net.aircommunity.platform.model.domain;

import com.fasterxml.jackson.annotation.JsonView;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.domain.Reviewable.ReviewStatus;
import net.aircommunity.platform.model.jaxb.TenantAdapter;
import net.aircommunity.platform.model.jaxb.UserAdapter;

/**
 * At an airport, the apron is the area of hard ground where aircraft are parked.
 *
 * @author Xiangwen.Kong
 */
@Entity
@Table(name = "air_platform_activity_message")
@XmlAccessorType(XmlAccessType.FIELD)
public class ActivityMessage extends Persistable {
	private static final long serialVersionUID = 1L;

	@Column(name = "title", length = ACTIVITY_MESSAGE_TITLE_LEN)
	private String title;

	@Column(name = "headings", length = ACTIVITY_MESSAGE_HEADINGS_LEN)
	private String headings;


	@Column(name = "thumbnails", length = IMAGE_URL_LEN)
	private String thumbnails;


	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "date", nullable = false)
	private Date date;

	@Lob
	@Column(name = "description")
	private String description;

	@Column(name = "published", nullable = false)
	@JsonView({JsonViews.Admin.class, JsonViews.Tenant.class})
	protected boolean published = false;

	@Enumerated(EnumType.STRING)
	@Column(name = "review_status", length = PRODUCT_REVIEW_STATUS_LEN, nullable = false)
	protected ReviewStatus reviewStatus = ReviewStatus.PENDING;

	@Size(max = DEFAULT_FIELD_LEN)
	@Column(name = "rejected_reason")
	protected String rejectedReason;


	@ManyToOne
	@JoinColumn(name = "tenant_id", nullable = false)
	@XmlJavaTypeAdapter(TenantAdapter.class)
	private Tenant vendor;


	public Tenant getVendor() {
		return vendor;
	}

	public void setVendor(Tenant vendor) {
		this.vendor = vendor;
	}

	public boolean isPublished() {
		return published;
	}

	public void setPublished(boolean published) {
		this.published = published;
	}

	public String getThumbnails() {
		return thumbnails;
	}

	public void setThumbnails(String thumbnails) {
		this.thumbnails = thumbnails;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getHeadings() {
		return headings;
	}

	public void setHeadings(String headings) {
		this.headings = headings;
	}


	public String getRejectedReason() {
		return rejectedReason;
	}

	public void setRejectedReason(String rejectedReason) {
		this.rejectedReason = rejectedReason;
	}

	public ReviewStatus getReviewStatus() {
		return reviewStatus;
	}

	public void setReviewStatus(ReviewStatus reviewStatus) {
		this.reviewStatus = reviewStatus;
	}



	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ActivityMessage [id=").append(id)
				.append(", thumbnails=").append(thumbnails)
				.append(", description=").append(description).append("]");
		return builder.toString();
	}


}
