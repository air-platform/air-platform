package net.aircommunity.platform.model.domain;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import io.micro.annotation.constraint.NotEmpty;

/**
 * At an airport, the apron is the area of hard ground where aircraft are parked.
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platform_apron", indexes = { @Index(name = "idx_city_published", columnList = "city,published") })
@XmlAccessorType(XmlAccessType.FIELD)
public class Apron extends Persistable {
	private static final long serialVersionUID = 1L;

	// apron name
	@NotEmpty
	@Size(max = PRODUCT_NAME_LEN)
	@Column(name = "name", length = PRODUCT_NAME_LEN, nullable = false)
	protected String name;

	// in which city
	@Size(max = CITY_NAME_LEN)
	@Column(name = "city", length = CITY_NAME_LEN, nullable = false)
	private String city;

	// apron type
	@Column(name = "type", length = APRON_TYPE_LEN, nullable = false)
	private Type type;

	// apron GPS location
	@Embedded
	private Location location;

	// whether it can be published or not
	// only published apron is visible
	@Column(name = "published", nullable = false)
	private boolean published = false;

	// apron description
	@Lob
	@Column(name = "description")
	private String description;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public boolean isPublished() {
		return published;
	}

	public void setPublished(boolean published) {
		this.published = published;
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
		builder.append("Apron [id=").append(id).append(", name=").append(name).append(", city=").append(city)
				.append(", type=").append(type).append(", location=").append(location).append(", published=")
				.append(published).append(", description=").append(description).append("]");
		return builder.toString();
	}

	/**
	 * apron type
	 */
	public enum Type {
		HELICOPTER, FIXEDWING;

		public static Type fromString(String value) {
			for (Type e : values()) {
				if (e.name().equalsIgnoreCase(value)) {
					return e;
				}
			}
			return null;
		}
	}

}
