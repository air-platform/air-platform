package net.aircommunity.platform.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
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

	// whether it can be published or not
	// e.g. product put on sale/pull off shelves
	@Column(name = "published", nullable = false)
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	protected boolean published = false;

	public boolean isPublished() {
		return published;
	}

	public void setPublished(boolean published) {
		this.published = published;
	}
}
