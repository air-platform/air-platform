package net.aircommunity.platform.model.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import io.micro.annotation.constraint.NotEmpty;
import net.aircommunity.platform.model.jaxb.DateAdapter;

/**
 * Course Model. <br>
 * 
 * TODO define instalments stage number, e.g. stage=3, and we will create 3 {@code Instalment} for {@code Enrollment}
 * (it need to extends {@code InstalmentOrder} )
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platform_course", indexes = {
		@Index(name = "idx_review_status_tenant_id", columnList = "review_status,tenant_id"),
		@Index(name = "idx_published_rank_score", columnList = "published,rank,start_date,score"),
		// XXX seems not selected, how to force
		// @Index(name = "idx_idx_location_aircraft_type", columnList = "published,location,aircraft_type")
})
@XmlAccessorType(XmlAccessType.FIELD)
public class Course extends StandardProduct {
	private static final long serialVersionUID = 1L;

	// TODO instalment stage number?
	// @Column(name = "instalments")
	// private int instalments;

	// aircraft type
	@NotEmpty
	@Size(max = AIRCRAFT_TYPE_LEN)
	@Column(name = "aircraft_type", length = AIRCRAFT_TYPE_LEN, nullable = false)
	private String aircraftType;

	// which city and may have address detail, FIXME: better just use city name
	@NotEmpty
	@Size(max = COURSE_LOCATION_LEN)
	@Column(name = "location", length = COURSE_LOCATION_LEN, nullable = false)
	private String location;

	// air driving license
	@NotEmpty
	@Size(max = AIRCRAFT_LICENSE_LEN)
	@Column(name = "license", length = AIRCRAFT_LICENSE_LEN, nullable = false)
	private String license;

	@NotNull
	@Temporal(value = TemporalType.DATE)
	@Column(name = "start_date", nullable = false)
	@XmlJavaTypeAdapter(DateAdapter.class)
	private Date startDate;

	@NotNull
	@Temporal(value = TemporalType.DATE)
	@Column(name = "end_date", nullable = false)
	@XmlJavaTypeAdapter(DateAdapter.class)
	private Date endDate;

	// enrollment requirement
	@Lob
	@Column(name = "enrollment")
	private String enrollment;

	@Lob
	@Column(name = "course_service")
	private String courseService;

	@Column(name = "total_num")
	private int totalNum;

	@Column(name = "enroll_num")
	private int enrollNum;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "school_id", nullable = false)
	private School school;

	public Course() {
	}

	public Course(String id) {
		this.id = id;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getEnrollment() {
		return enrollment;
	}

	public void setEnrollment(String enrollment) {
		this.enrollment = enrollment;
	}

	public String getCourseService() {
		return courseService;
	}

	public void setCourseService(String courseService) {
		this.courseService = courseService;
	}

	public int getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(int totalNum) {
		this.totalNum = totalNum;
	}

	public int getEnrollNum() {
		return enrollNum;
	}

	public void setEnrollNum(int enrollNum) {
		this.enrollNum = enrollNum;
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

	public School getSchool() {
		return school;
	}

	public void setSchool(School school) {
		this.school = school;
	}

	@PrePersist
	private void prePersist() {
		setType(Type.COURSE);
		setCategory(Category.AIR_TRAINING);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Course [id=").append(id).append(", price=").append(price).append(", currencyUnit=")
				.append(currencyUnit).append(", name=").append(name).append(", type=").append(type)
				.append(", category=").append(category).append(", image=").append(image).append(", stock=")
				.append(stock).append(", score=").append(score).append(", totalSales=").append(totalSales)
				.append(", rank=").append(rank).append(", published=").append(published).append(", creationDate=")
				.append(creationDate).append(", clientManagers=").append(clientManagers).append(", description=")
				.append(description).append(", reviewStatus=").append(reviewStatus).append(", rejectedReason=")
				.append(rejectedReason).append(", aircraftType=").append(aircraftType).append(", location=")
				.append(location).append(", license=").append(license).append(", startDate=").append(startDate)
				.append(", endDate=").append(endDate).append(", enrollment=").append(enrollment)
				.append(", courseService=").append(courseService).append(", totalNum=").append(totalNum)
				.append(", enrollNum=").append(enrollNum).append("]");
		return builder.toString();
	}
}
