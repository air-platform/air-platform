package net.aircommunity.platform.model.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Airport information managed by ADMIN.
 * 
 * @author Bin.Zhang
 */
@Entity
//@formatter:off
@Table(name = "air_platfrom_airport", indexes = { 
	@Index(name = "idx_name", columnList = "name"),
	@Index(name = "idx_city_and_name", columnList = "city,name"),
	@Index(name = "idx_icao4", columnList = "icao4", unique = true), 
	@Index(name = "idx_iata3", columnList = "iata3"),
	@Index(name = "idx_city", columnList = "city") 
})
//@formatter:on
@XmlAccessorType(XmlAccessType.FIELD)
public class Airport extends Persistable {
	private static final long serialVersionUID = 1L;

	// airport name
	@NotNull
	@Column(name = "name", nullable = false)
	private String name;

	// of which city
	@NotNull
	@Column(name = "city", nullable = false)
	private String city;

	// of which country
	@NotNull
	@Column(name = "country", nullable = false)
	private String country;

	// IATA 3-Letter Code
	@Column(name = "iata3")
	private String iata3;

	// ICAO 4-Letter Code
	@Column(name = "icao4", nullable = false, unique = true)
	private String icao4;

	// Decimal degrees, usually to six significant digits. Negative is South, positive is North.
	@Column(name = "lat")
	private double latitude;

	// Decimal degrees, usually to six significant digits. Negative is West, positive is East.
	@Column(name = "lon")
	private double longitude;

	// Hours offset from UTC. Fractional hours are expressed as decimals, eg. India is 5.5.
	@Column(name = "timezone")
	private String timezone;

	public Airport() {
	}

	public Airport(String id) {
		this.id = id;
	}

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

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getIata3() {
		return iata3;
	}

	public void setIata3(String iata3) {
		this.iata3 = iata3;
	}

	public String getIcao4() {
		return icao4;
	}

	public void setIcao4(String icao4) {
		this.icao4 = icao4;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Airport [name=").append(name).append(", city=").append(city).append(", country=")
				.append(country).append(", iata3=").append(iata3).append(", icao4=").append(icao4).append(", latitude=")
				.append(latitude).append(", longitude=").append(longitude).append(", timezone=").append(timezone)
				.append(", id=").append(id).append("]");
		return builder.toString();
	}

}
