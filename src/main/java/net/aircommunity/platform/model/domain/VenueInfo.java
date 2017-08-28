package net.aircommunity.platform.model.domain;

import io.micro.annotation.constraint.NotEmpty;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * @author Xiangwen.Kong
 */
@Entity
@Table(name = "air_platform_venue_info")
@XmlAccessorType(XmlAccessType.FIELD)
public class VenueInfo extends Persistable {
	private static final long serialVersionUID = 1L;

	// venue name
	@NotEmpty
	@Size(max = PRODUCT_NAME_LEN)
	@Column(name = "name", length = PRODUCT_NAME_LEN, nullable = false)
	private String name;


	// venue picture
	@Column(name = "picture", length = IMAGE_URL_LEN)
	private String picture;

	// venue description
	@Lob
	@Column(name = "description")
	private String description;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("VenueTemplate [id=").append(id).append(", name=").append(name)
				.append(", picture=").append(picture)
				.append(", description=").append(description).append("]");
		return builder.toString();
	}


}
