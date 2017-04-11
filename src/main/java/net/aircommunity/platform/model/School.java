package net.aircommunity.platform.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Created by guankai on 11/04/2017.
 */
@Entity
@Table(name = "air_platform_school")
@XmlAccessorType(XmlAccessType.FIELD)
public class School extends Persistable{

    @Column(name = "school_name",nullable = false)
    private String schoolName;

    @Lob
    @Column(name = "school_decs",nullable = false)
    private String schoolDesc;

    @Column(name = "image_url",nullable = false)
    private String imageUrl;

    @Column(name = "address")
    private String address;

    @Column(name = "contact")
    private String contact;

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSchoolDesc() {
        return schoolDesc;
    }

    public void setSchoolDesc(String schoolDesc) {
        this.schoolDesc = schoolDesc;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
