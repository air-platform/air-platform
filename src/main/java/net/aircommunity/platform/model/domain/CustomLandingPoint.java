package net.aircommunity.platform.model.domain;

import io.micro.annotation.constraint.NotEmpty;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import net.aircommunity.platform.model.domain.Reviewable.ReviewStatus;
import net.aircommunity.platform.model.jaxb.UserAdapter;

/**
 * At an airport, the apron is the area of hard ground where aircraft are parked.
 *
 * @author Xiangwen.Kong
 */
@Entity
@Table(name = "air_platform_custom_landingpoint")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomLandingPoint extends Persistable {
	private static final long serialVersionUID = 1L;

	// apron name
	@NotEmpty
	@Size(max = PRODUCT_NAME_LEN)
	@Column(name = "name", length = PRODUCT_NAME_LEN, nullable = false)
	private String name;


	// apron GPS location
	@Embedded
	private Location location;

	// apron address
	@Lob
	@Column(name = "address")
	private String address;

	@Lob
	@Column(name = "images")
	private String images;

	// apron description
	@Lob
	@Column(name = "description")
	private String description;


	@ManyToOne
	@JoinColumn(name = "user_id")
	@XmlJavaTypeAdapter(UserAdapter.class)
	protected User user;


	@Enumerated(EnumType.STRING)
	@Column(name = "review_status", length = PRODUCT_REVIEW_STATUS_LEN, nullable = false)
	protected ReviewStatus reviewStatus = ReviewStatus.PENDING;

	@Size(max = DEFAULT_FIELD_LEN)
	@Column(name = "rejected_reason")
	protected String rejectedReason;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}


	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getImages() {
		return images;
	}

	public void setImages(String images) {
		this.images = images;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Apron [id=").append(id).append(", name=").append(name)
				.append(", address=").append(address).append(", images=").append(images)
				.append(", location=").append(location)
				.append(", description=").append(description).append("]");
		return builder.toString();
	}


}
