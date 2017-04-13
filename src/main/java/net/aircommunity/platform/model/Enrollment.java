package net.aircommunity.platform.model;

import net.aircommunity.platform.model.jaxb.AccountAdapter;
import net.aircommunity.platform.model.jaxb.DateAdapter;

import javax.persistence.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;

/**
 * Created by guankai on 12/04/2017.
 */
@Entity
@Table(name = "air_platform_enrollment")
public class Enrollment extends Order {

    @Column(name = "air_type",nullable = false)
    private String airType;

    @Column(name = "license",nullable = false)
    private String license;

    @Column(name = "location",nullable = false)
    private String location;

    @XmlJavaTypeAdapter(AccountAdapter.class)
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "name")
    private String name;

    @Column(name = "identify")
    private String identify;


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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentify() {
        return identify;
    }

    public void setIdentify(String identify) {
        this.identify = identify;
    }

}
