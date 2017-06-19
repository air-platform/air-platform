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
//@formatter:off
@Table(name = "air_platform_course", indexes = { 
		@Index(name = "idx_aircraft_type", columnList = "aircraft_type"),
		@Index(name = "idx_location", columnList = "location"), 
		@Index(name = "idx_license", columnList = "license") 
})
//@formatter:on
@XmlAccessorType(XmlAccessType.FIELD)
public class Course extends StandardProduct {
	private static final long serialVersionUID = 1L;

	// TODO 分期付款 instalment stage number?
	// @Column(name = "instalments")
	// private int instalments;

	// aircraft type
	@NotEmpty
	@Column(name = "aircraft_type", nullable = false)
	private String aircraftType;

	// which city
	@NotEmpty
	@Column(name = "location", nullable = false)
	private String location;

	// air driving license
	@NotEmpty
	@Column(name = "license", nullable = false)
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
		setCategory(Category.AIR_TRAINING);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Course [aircraftType=").append(aircraftType).append(", location=").append(location)
				.append(", license=").append(license).append(", startDate=").append(startDate).append(", endDate=")
				.append(endDate).append(", enrollment=").append(enrollment).append(", courseService=")
				.append(courseService).append(", totalNum=").append(totalNum).append(", enrollNum=").append(enrollNum)
				.append(", school=").append(school).append(", price=").append(price).append(", currencyUnit=")
				.append(currencyUnit).append(", name=").append(name).append(", image=").append(image).append(", score=")
				.append(score).append(", totalSales=").append(totalSales).append(", rank=").append(rank)
				.append(", published=").append(published).append(", creationDate=").append(creationDate)
				.append(", clientManagers=").append(clientManagers).append(", description=").append(description)
				.append(", reviewStatus=").append(reviewStatus).append(", rejectedReason=").append(rejectedReason)
				.append(", id=").append(id).append("]");
		return builder.toString();
	}
}
