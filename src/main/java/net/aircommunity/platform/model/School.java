package net.aircommunity.platform.model;

import net.aircommunity.platform.model.jaxb.AccountAdapter;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Created by guankai on 11/04/2017.
 */
@Entity
@Table(name = "air_platform_school")
@XmlAccessorType(XmlAccessType.FIELD)
public class School extends Persistable {

    @Column(name = "school_name", nullable = false)
    private String schoolName;

    @Lob
    @Column(name = "school_decs", nullable = false)
    private String schoolDesc;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "address")
    private String address;

    @Column(name = "contact")
    private String contact;

    @XmlJavaTypeAdapter(AccountAdapter.class)
    @ManyToOne
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

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

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

}
