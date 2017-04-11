package net.aircommunity.platform.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.aircommunity.platform.model.jaxb.AccountAdapter;

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

	// Flight NO.
	@Column(name = "flight_no", nullable = false, unique = true)
	private String flightNo;

	// e.g. G450 in Chinese
	@Column(name = "name", nullable = false)
	private String name;

	// e.g. Gulfstream 450
	@Column(name = "aircraft_type", nullable = false)
	private String aircraftType;

	// start from price +
	@Column(name = "price")
	private int price;

	@Column(name = "unit", nullable = false)
	@Enumerated(EnumType.STRING)
	private CurrencyUnit unit = CurrencyUnit.RMB;

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

	@Lob
	@Column(name = "description")
	private String description;

	// List<appearance>...

	@XmlJavaTypeAdapter(AccountAdapter.class)
	@ManyToOne
	@JoinColumn(name = "tenant_id", nullable = false)
	private Tenant owner;

	public String getFlightNo() {
		return flightNo;
	}

	public void setFlightNo(String flightNo) {
		this.flightNo = flightNo;
	}

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

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public CurrencyUnit getUnit() {
		return unit;
	}

	public void setUnit(CurrencyUnit unit) {
		this.unit = unit;
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

	public Tenant getOwner() {
		return owner;
	}

	public void setOwner(Tenant owner) {
		this.owner = owner;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Fleet [flightNo=").append(flightNo).append(", name=").append(name).append(", aircraftType=")
				.append(aircraftType).append(", price=").append(price).append(", unit=").append(unit)
				.append(", capacity=").append(capacity).append(", weight=").append(weight).append(", beds=")
				.append(beds).append(", facilities=").append(facilities).append(", fullloadRange=")
				.append(fullloadRange).append(", description=").append(description).append("]");
		return builder.toString();
	}
}
