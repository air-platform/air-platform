package net.aircommunity.platform.model.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.aircommunity.platform.model.jaxb.VenueTemplateAdapter;

/**
 * @author Xiangwen.Kong
 */
@Entity
@Table(name = "air_platform_venue_template_coupon_user")
@XmlAccessorType(XmlAccessType.FIELD)
public class VenueTemplateCouponUser extends Persistable {
	private static final long serialVersionUID = 1L;

	@ManyToOne
	@JoinColumn(name = "venue_template_id")
	@XmlJavaTypeAdapter(VenueTemplateAdapter.class)
	protected VenueTemplate venueTemplate;

	@ManyToOne
	@JoinColumn(name = "user_id")
	@XmlTransient
	protected Account user;

	@Transient
	@XmlElement(name = "user")
	private String userId;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Column(name = "coupon_num", nullable = false)
	private int couponNum = 1;

	@Column(name = "points_per_coupon", nullable = false)
	private int pointsPerCoupon = 0;

	@PostLoad
	private void onLoad() {
		userId = user.getId();
	}

	public VenueTemplateCouponUser() {
	}

	public VenueTemplateCouponUser(String id) {
		this.id = id;
	}

	public VenueTemplate getVenueTemplate() {
		return venueTemplate;
	}

	public void setVenueTemplate(VenueTemplate venueTemplate) {
		this.venueTemplate = venueTemplate;
	}

	public Account getUser() {
		return user;
	}

	public void setUser(Account user) {
		this.user = user;
	}

	public int getCouponNum() {
		return couponNum;
	}

	public void setCouponNum(int couponNum) {
		this.couponNum = couponNum;
	}

	public int getPointsPerCoupon() {
		return pointsPerCoupon;
	}

	public void setPointsPerCoupon(int pointsPerCoupon) {
		this.pointsPerCoupon = pointsPerCoupon;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("VenueTemplateCouponUser [id=").append(id).append("]");
		return builder.toString();
	}

}
