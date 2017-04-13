package net.aircommunity.platform.model;

import net.aircommunity.platform.model.jaxb.AccountAdapter;
import net.aircommunity.platform.model.jaxb.DateAdapter;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;

/**
 * Created by guankai on 12/04/2017.
 */
@Entity
@Table(name = "air_platform_course")
@XmlAccessorType(XmlAccessType.FIELD)
public class Course extends Product {

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @XmlJavaTypeAdapter(DateAdapter.class)
    @Temporal(value = TemporalType.DATE)
    @Column(name = "start_date", nullable = false)
    private Date startDate;

    @XmlJavaTypeAdapter(DateAdapter.class)
    @Temporal(value = TemporalType.DATE)
    @Column(name = "end_date", nullable = false)
    private Date endDate;

    @XmlJavaTypeAdapter(AccountAdapter.class)
    @ManyToOne
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @Column(name = "enrollment")
    private String enrollment;

    @Column(name = "course_service")
    private String courseService;

    @Column(name = "total_num")
    private int totalNum;

    @Column(name = "enroll_num")
    private int enrollNum;

    @Column(name = "air_type", nullable = false)
    private String airType;

    @Column(name = "license", nullable = false)
    private String license;

    @Column(name = "location", nullable = false)
    private String location;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
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
}
