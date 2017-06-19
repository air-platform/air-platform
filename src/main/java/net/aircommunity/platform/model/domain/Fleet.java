package net.aircommunity.platform.model.domain;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import io.micro.annotation.constraint.NotEmpty;

/**
 * Fleet Information for {@code Charter}
 * 
 * @author Bin.Zhang
 */
@Entity
@XmlAccessorType(XmlAccessType.FIELD)
@Table(name = "air_platfrom_fleet", indexes = { @Index(name = "idx_aircraft_type", columnList = "aircraft_type") })
public class Fleet extends StandardProduct {
	private static final long serialVersionUID = 1L;

	// Flight NO. is global unique for all air company
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

	// XXX should be int for a specific fleet with same aircaftType, e.g. G550 ?
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
	@Lob
	@Column(name = "facilities")
	private String facilities;

	// images of this Aircraft, comma separated, image1.png,image2/png
	@Lob
	@Column(name = "appearances")
	private String appearances;

	// interior 360 of this Aircraft, 360view.html
	@Column(name = "interior")
	private String interior;

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
	public Category getCategory() {
		if (category == null) {
			return Category.AIR_JET;
		}
		return category;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Fleet [flightNo=").append(flightNo).append(", aircraftType=").append(aircraftType)
				.append(", location=").append(location).append(", status=").append(status).append(", capacity=")
				.append(capacity).append(", beds=").append(beds).append(", weight=").append(weight)
				.append(", fullloadRange=").append(fullloadRange).append(", facilities=").append(facilities)
				.append(", appearances=").append(appearances).append(", interior=").append(interior).append(", price=")
				.append(price).append(", currencyUnit=").append(currencyUnit).append(", name=").append(name)
				.append(", image=").append(image).append(", score=").append(score).append(", totalSales=")
				.append(totalSales).append(", rank=").append(rank).append(", published=").append(published)
				.append(", creationDate=").append(creationDate).append(", clientManagers=").append(clientManagers)
				.append(", description=").append(description).append(", reviewStatus=").append(reviewStatus)
				.append(", rejectedReason=").append(rejectedReason).append(", id=").append(id).append("]");
		return builder.toString();
	}

	/**
	 * Flight status
	 */
	public enum Status {
		UNAVAILABLE, AVAILABLE;

		// According to JSR 311 spec, if used in @QueryParam, fromString is a naming conversion
		public static Status fromString(String source) {
			for (Status e : values()) {
				if (e.name().equalsIgnoreCase(source)) {
					return e;
				}
			}
			return null;
		}
	}
}