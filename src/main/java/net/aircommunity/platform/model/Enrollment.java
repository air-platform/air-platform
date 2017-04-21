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
 * Created by guankai on 12/04/2017.
 */
@Entity
@XmlAccessorType(XmlAccessType.FIELD)
@Table(name = "air_platform_course_enrollment")
public class Enrollment extends VendorAwareOrder {
	private static final long serialVersionUID = 1L;

	@NotEmpty
	@Column(name = "air_type", nullable = false)
	private String airType;

	@NotEmpty
	@Column(name = "license", nullable = false)
	private String license;

	@NotEmpty
	@Column(name = "location", nullable = false)
	private String location;

	// person name
	@NotEmpty
	@Column(name = "person", nullable = false)
	private String person;

	@NotEmpty
	@Column(name = "identity")
	private String identity;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "course_id", nullable = false)
	private Course course;

	public Enrollment() {
	}

	public Enrollment(String id) {
		this.id = id;
	}

	public String getAirType() {
		return airType;
	}

	public void setAirType(String airType) {
		this.airType = airType;
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

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	@Override
	public Product getProduct() {
		return course;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Enrollment [airType=").append(airType).append(", license=").append(license)
				.append(", location=").append(location).append(", person=").append(person).append(", identity=")
				.append(identity).append(", orderNo=").append(orderNo).append(", status=").append(status)
				.append(", creationDate=").append(creationDate).append(", paymentDate=").append(paymentDate)
				.append(", finishedDate=").append(finishedDate).append(", note=").append(note).append(", id=")
				.append(id).append("]");
		return builder.toString();
	}

}
