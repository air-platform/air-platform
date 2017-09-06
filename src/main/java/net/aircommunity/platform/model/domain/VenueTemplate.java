package net.aircommunity.platform.model.domain;

import io.micro.annotation.constraint.NotEmpty;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * @author Xiangwen.Kong
 */
@Entity
@Table(name = "air_platform_venue_template")
@XmlAccessorType(XmlAccessType.FIELD)
public class VenueTemplate extends Persistable {
	private static final long serialVersionUID = 1L;

	// venue template name
	@NotEmpty
	@Size(max = PRODUCT_NAME_LEN)
	@Column(name = "name", length = PRODUCT_NAME_LEN, nullable = false)
	private String name;


	// venue template address
	@Column(name = "background_pic", length = IMAGE_URL_LEN)
	private String backgroundPic;

	@Column(name = "background_color", length = COLOR_LEN)
	private String backgroundColor;


	@OneToMany(mappedBy = "venueTemplate", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	List<VenueInfo> venueInfos;

	// venue template description
	@Lob
	@Column(name = "description")
	private String description;

	public VenueTemplate(String id) {
		this.id = id;
	}

	public VenueTemplate() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBackgroundPic() {
		return backgroundPic;
	}

	public void setBackgroundPic(String backgroundPic) {
		this.backgroundPic = backgroundPic;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
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
				.append(", backgroundPic=").append(backgroundPic)
				.append(", backgroundColor=").append(backgroundColor)
				.append(", description=").append(description).append("]");
		return builder.toString();
	}


}
