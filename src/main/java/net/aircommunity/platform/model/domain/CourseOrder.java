package net.aircommunity.platform.model.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import io.micro.annotation.constraint.NotEmpty;
import net.aircommunity.platform.model.domain.Product.Type;

/**
 * {@code Course} order (enrollment) of a {@code School}
 * 
 * @author guankai
 */
@Entity
@XmlAccessorType(XmlAccessType.FIELD)
@Table(name = "air_platform_course_order", indexes = {
		@Index(name = "idx_user_id_status", columnList = "user_id,status,creation_date"),
		@Index(name = "idx_tenant_id_status", columnList = "tenant_id,status,creation_date"),
		@Index(name = "idx_course_id_status", columnList = "course_id,status,creation_date")
		//
})
public class CourseOrder extends VendorAwareOrder {
	private static final long serialVersionUID = 1L;

	@NotEmpty
	@Size(max = 255)
	@Column(name = "aircraft_type", nullable = false)
	private String aircraftType;

	@NotEmpty
	@Size(max = 255)
	@Column(name = "license", nullable = false)
	private String license;

	@NotEmpty
	@Size(max = 255)
	@Column(name = "location", nullable = false)
	private String location;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "course_id", nullable = false)
	private Course course;

	public CourseOrder() {
	}

	public CourseOrder(String id) {
		this.id = id;
	}

	public String getAircraftType() {
		return aircraftType;
	}

	public void setAircraftType(String aircraftType) {
		this.aircraftType = aircraftType;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@Override
	public Course getProduct() {
		return course;
	}

	@Override
	protected void doSetProduct(Product product) {
		course = (Course) product;
	}

	@PrePersist
	private void prePersist() {
		setType(Type.COURSE);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CourseOrder [aircraftType=").append(aircraftType).append(", license=").append(license)
				.append(", location=").append(location).append(", contact=").append(contact).append(", orderNo=")
				.append(orderNo).append(", status=").append(status).append(", creationDate=").append(creationDate)
				.append(", paymentDate=").append(paymentDate).append(", finishedDate=").append(finishedDate)
				.append(", note=").append(note).append(", id=").append(id).append("]");
		return builder.toString();
	}
}
