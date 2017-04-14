package net.aircommunity.platform.model;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.Table;

import net.aircommunity.platform.model.constraint.NotEmpty;

/**
 * Fleet Information for {@code Charter}
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platfrom_fleet", indexes = { @Index(name = "idx_aircraft_type", columnList = "aircraft_type") })
public class Fleet extends PricedProduct {
	private static final long serialVersionUID = 1L;

	// properties inherited:
	// name: e.g. G450 in Chinese
	// price
	// desc

	// Flight NO.
	// this is global unique for all air company
	@NotEmpty
	@Column(name = "flight_no", nullable = false, unique = true)
	private String flightNo;

	// e.g. Gulfstream 450 (should select from a predefined list)
	@NotEmpty
	@Column(name = "aircraft_type", nullable = false)
	private String aircraftType;

	// Aircraft GPS location
	@Embedded
	private Location location;

	// Aircraft current status
	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private Status status = Status.AVAILABLE;

	// e.g. 11 - 14 guests
	@Column(name = "capacity")
	private String capacity;

	// e.g. 2 divans can be made as beds
	@Column(name = "beds")
	private int beds;

	// e.g. 430 kilograms of baggage
	@Column(name = "weight")
	private int weight;

	// e.g. Full-load range of about 8061 kilometers, amount to the distance from Beijing to Kuala Lumpur.
	@Column(name = "fullload_range")
	private int fullloadRange;

	// e.g. Satellite phone, WIFI, wine cooler, DVD/CD player, external IPOD player, LCD monitor.
	@Column(name = "facilities")
	private String facilities;

	// images of this Aircraft, comma separated, image1.png,image2/png
	@Column(name = "appearances")
	private String appearances;

	// interior 360 of this Aircraft, 360view.html
	@Column(name = "interior")
	private String interior;

	// TODO accountManagers

	public Fleet() {
	}

	public Fleet(String id) {
		this.id = id;
	}

	public String getFlightNo() {
		return flightNo;
	}

	public void setFlightNo(String flightNo) {
		this.flightNo = flightNo;
	}

	public String getAircraftType() {
		return aircraftType;
	}

	public void setAircraftType(String aircraftType) {
		this.aircraftType = aircraftType;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getCapacity() {
		return capacity;
	}

	public void setCapacity(String capacity) {
		this.capacity = capacity;
	}

	public int getBeds() {
		return beds;
	}

	public void setBeds(int beds) {
		this.beds = beds;
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

	public String getFacilities() {
		return facilities;
	}

	public void setFacilities(String facilities) {
		this.facilities = facilities;
	}

	public String getAppearances() {
		return appearances;
	}

	public void setAppearances(String appearances) {
		this.appearances = appearances;
	}

	public String getInterior() {
		return interior;
	}

	public void setInterior(String interior) {
		this.interior = interior;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Fleet [flightNo=").append(flightNo).append(", aircraftType=").append(aircraftType)
				.append(", location=").append(location).append(", status=").append(status).append(", capacity=")
				.append(capacity).append(", beds=").append(beds).append(", weight=").append(weight)
				.append(", fullloadRange=").append(fullloadRange).append(", facilities=").append(facilities)
				.append(", appearances=").append(appearances).append(", interior=").append(interior).append(", name=")
				.append(name).append(", price=").append(price).append(", currencyUnit=").append(currencyUnit)
				.append(", description=").append(description).append(", id=").append(id).append("]");
		return builder.toString();
	}

	/**
	 * Flight status
	 */
	public enum Status {
		UNAVAILABLE, AVAILABLE
	}
}
