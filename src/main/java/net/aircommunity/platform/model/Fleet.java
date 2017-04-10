package net.aircommunity.platform.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Fleet Information
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platfrom_fleet")
@XmlAccessorType(XmlAccessType.FIELD)
public class Fleet extends Persistable {
	private static final long serialVersionUID = 1L;

	// e.g. G450 in Chinese
	private String name;

	// e.g. Gulfstream 450
	private String aircraftType;

	// e.g. 11 - 14 guests
	private String capacity;

	// e.g. 430 kilograms of baggage
	private int weight;

	// e.g. 2 divans can be made as beds
	private int beds;

	// e.g. Satellite phone, WIFI, wine cooler, DVD/CD player, external IPOD player, LCD monitor.
	private String facilities;

	// e.g. Full-load range of about 8061 kilometers, amount to the distance from Beijing to Kuala Lumpur.
	private int fullloadRange;

	private String description;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAircraftType() {
		return aircraftType;
	}

	public void setAircraftType(String aircraftType) {
		this.aircraftType = aircraftType;
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

	public int getBeds() {
		return beds;
	}

	public void setBeds(int beds) {
		this.beds = beds;
	}

	public String getFacilities() {
		return facilities;
	}

	public void setFacilities(String facilities) {
		this.facilities = facilities;
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
		builder.append("Fleet [name=").append(name).append(", aircraftType=").append(aircraftType).append(", capacity=")
				.append(capacity).append(", weight=").append(weight).append(", beds=").append(beds)
				.append(", facilities=").append(facilities).append(", fullloadRange=").append(fullloadRange)
				.append(", description=").append(description).append("]");
		return builder.toString();
	}

}
