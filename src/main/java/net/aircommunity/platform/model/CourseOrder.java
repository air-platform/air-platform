package net.aircommunity.platform.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import io.micro.annotation.constraint.NotEmpty;

/**
 * {@code Course} Enrollment of a {@code School}
 * 
 * @author guankai
 */
@Entity
@XmlAccessorType(XmlAccessType.FIELD)
@Table(name = "air_platform_course_order")
public class CourseOrder extends VendorAwareOrder {
	private static final long serialVersionUID = 1L;

	@NotEmpty
	@Column(name = "aircraft_type", nullable = false)
	private String aircraftType;

	@NotEmpty
	@Column(name = "license", nullable = false)
	private String license;

	@NotEmpty
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
	public Type getType() {
		return Type.COURSE;
	}

	@Override
	public Course getProduct() {
		return course;
	}

	@Override
	protected void doSetProduct(Product product) {
		course = (Course) product;
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
