package net.aircommunity.platform.model.domain;

import io.micro.annotation.constraint.NotEmpty;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import net.aircommunity.platform.model.jaxb.VenueTemplateAdapter;

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

	@Column(name = "background_color", length = COLOR_LEN)
	private String backgroundColor;

	// venue description
	@Lob
	@Column(name = "description")
	private String description;

	// @XmlElement
	@ManyToOne
	@JoinColumn(name = "venue_template_id")
	@XmlJavaTypeAdapter(VenueTemplateAdapter.class)
	protected VenueTemplate venueTemplate;

	@OneToMany(mappedBy = "venueInfo", fetch = FetchType.EAGER)
	List<VenueCategory> venueCategories;

	public List<VenueCategory> getVenueCategories() {
		return venueCategories;
	}

	public void setVenueCategories(List<VenueCategory> venueCategories) {
		this.venueCategories = venueCategories;
	}

	public VenueInfo(String id) {
		this.id = id;
	}

	public VenueInfo() {
	}

	public VenueTemplate getVenueTemplate() {
		return venueTemplate;
	}

	public void setVenueTemplate(VenueTemplate venueTemplate) {
		this.venueTemplate = venueTemplate;
	}

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

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("VenueInfo [id=").append(id).append(", name=").append(name).append(", picture=").append(picture)
				.append(", description=").append(description).append("]");
		return builder.toString();
	}

}
