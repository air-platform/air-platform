package net.aircommunity.platform.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import net.aircommunity.platform.model.constraint.NotEmpty;

/**
 * Fleet Information for {@code Charter}
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platfrom_fleet")
@XmlAccessorType(XmlAccessType.FIELD)
public class Fleet extends Product {
	private static final long serialVersionUID = 1L;

	// properties inherited:
	// name: e.g. G450 in Chinese
	// price
	// desc

	// TODO images of this fleet
	// appearance
	// interior
	// interior-360

	// Flight NO.
	@NotEmpty
	@Column(name = "flight_no", nullable = false, unique = true)
	private String flightNo;

	// e.g. Gulfstream 450
	@NotEmpty
	@Column(name = "aircraft_type", nullable = false)
	private String aircraftType;

	// e.g. 11 - 14 guests
	@Column(name = "capacity")
	private String capacity;

	// e.g. 430 kilograms of baggage
	@Column(name = "weight")
	private int weight;

	// e.g. 2 divans can be made as beds
	@Column(name = "beds")
	private int beds;

	// e.g. Satellite phone, WIFI, wine cooler, DVD/CD player, external IPOD player, LCD monitor.
	@Column(name = "facilities")
	private String facilities;

	// e.g. Full-load range of about 8061 kilometers, amount to the distance from Beijing to Kuala Lumpur.
	@Column(name = "fullload_range")
	private int fullloadRange;

	// List<appearance>...

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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Fleet [flightNo=").append(flightNo).append(", aircraftType=").append(aircraftType)
				.append(", capacity=").append(capacity).append(", weight=").append(weight).append(", beds=")
				.append(beds).append(", facilities=").append(facilities).append(", fullloadRange=")
				.append(fullloadRange).append(", name=").append(name).append(", price=").append(price)
				.append(", currencyUnit=").append(currencyUnit).append(", description=").append(description)
				.append(", id=").append(id).append("]");
		return builder.toString();
	}
}
