package net.aircommunity.platform.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * Reviewable entity of a TENANT, it should be review by ADMIN before it can be published.
 * 
 * @author Bin.Zhang
 */
@MappedSuperclass
@XmlAccessorType(XmlAccessType.FIELD)
public class Reviewable extends Persistable {
	private static final long serialVersionUID = 1L;

	// whether a product is approved or not by platform ADMIN
	@Column(name = "approved", nullable = false)
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	protected boolean approved = false;

	// reason when it's rejected by platform ADMIN
	@Size(max = 1000)
	@Column(name = "rejected_reason", nullable = false, length = 1000)
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	protected String rejectedReason;

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public String getRejectedReason() {
		return rejectedReason;
	}

	public void setRejectedReason(String rejectedReason) {
		this.rejectedReason = rejectedReason;
	}

}
