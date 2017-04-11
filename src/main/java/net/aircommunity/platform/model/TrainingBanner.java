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
@Table(name = "air_platform_training_banner")
@XmlAccessorType(XmlAccessType.FIELD)
public class TrainingBanner extends Persistable {

    @Column(name = "banner_name", nullable = false)
    private String bannerName;

    @Lob
    @Column(name = "banner_desc")
    private String bannerDesc;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "banner_url")
    private String bannerUrl;

    public String getBannerName() {
        return bannerName;
    }

    public void setBannerName(String bannerName) {
        this.bannerName = bannerName;
    }

    public String getBannerDesc() {
        return bannerDesc;
    }

    public void setBannerDesc(String bannerDesc) {
        this.bannerDesc = bannerDesc;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }
}
