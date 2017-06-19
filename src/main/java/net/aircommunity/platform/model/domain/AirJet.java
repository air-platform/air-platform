package net.aircommunity.platform.model.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import io.micro.annotation.constraint.NotEmpty;

/**
 * The information of {@code Fleet} managed by ADMIN.
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platfrom_airjet", indexes = { @Index(name = "idx_type", columnList = "type") })
@XmlAccessorType(XmlAccessType.FIELD)
public class AirJet extends Persistable {
	private static final long serialVersionUID = 1L;

	// e.g. G550
	@NotEmpty
	@Column(name = "type", nullable = false, unique = true)
	private String type;

	// human friendly name
	@NotEmpty
	@Column(name = "name", nullable = false)
	private String name;

	// product image
	@NotEmpty
	@Column(name = "image", nullable = false)
	private String image;

	// e.g. 11 - 14 guests
	@NotEmpty
	@Column(name = "capacity", nullable = false)
	private String capacity;

	// e.g. 430 kilograms of baggage
	@Min(0)
	@Column(name = "weight", nullable = false)
	private int weight;

	// e.g. Full-load range of about 8061 kilometers, amount to the distance from Beijing to Kuala Lumpur.
	@Min(0)
	@Column(name = "fullload_range", nullable = false)
	private int fullloadRange;

	// description
	@Lob
	@Column(name = "description")
	private String description;

	public AirJet() {
	}

	public AirJet(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getCapacity() {
		return capacity;
	}

	public void setCapacity(String capacity) {
		this.capacity = capacity;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getFullloadRange() {
		return fullloadRange;
	}

	public void setFullloadRange(int fullloadRange) {
		this.fullloadRange = fullloadRange;
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
		builder.append("AirJet [type=").append(type).append(", name=").append(name).append(", image=").append(image)
				.append(", capacity=").append(capacity).append(", weight=").append(weight).append(", fullloadRange=")
				.append(fullloadRange).append(", description=").append(description).append(", id=").append(id)
				.append("]");
		return builder.toString();
	}
}
