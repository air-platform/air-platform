package net.aircommunity.platform.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
 * Created by guankai on 12/04/2017.
 */
@Entity
@Table(name = "air_platform_course")
@XmlAccessorType(XmlAccessType.FIELD)
public class Course extends PricedProduct {
	private static final long serialVersionUID = 1L;

	@NotNull
	@XmlJavaTypeAdapter(DateAdapter.class)
	@Temporal(value = TemporalType.DATE)
	@Column(name = "start_date", nullable = false)
	private Date startDate;

	@NotNull
	@XmlJavaTypeAdapter(DateAdapter.class)
	@Temporal(value = TemporalType.DATE)
	@Column(name = "end_date", nullable = false)
	private Date endDate;

	@Column(name = "enrollment")
	private String enrollment;

	@Column(name = "course_service")
	private String courseService;

	@Column(name = "total_num")
	private int totalNum;

	@Column(name = "enroll_num")
	private int enrollNum;

	@NotEmpty
	@Column(name = "air_type", nullable = false)
	private String airType;

	@NotEmpty
	@Column(name = "license", nullable = false)
	private String license;

	@NotEmpty
	@Column(name = "location", nullable = false)
	private String location;

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

	public School getSchool() {
		return school;
	}

	public void setSchool(School school) {
		this.school = school;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Course [startDate=").append(startDate).append(", endDate=").append(endDate).append(", image=")
				.append(image).append(", enrollment=").append(enrollment).append(", courseService=")
				.append(courseService).append(", totalNum=").append(totalNum).append(", enrollNum=").append(enrollNum)
				.append(", airType=").append(airType).append(", license=").append(license).append(", location=")
				.append(location).append(", price=").append(price).append(", currencyUnit=").append(currencyUnit)
				.append(", name=").append(name).append(", score=").append(score).append(", creationDate=")
				.append(creationDate).append(", description=").append(description).append(", id=").append(id)
				.append("]");
		return builder.toString();
	}
}
