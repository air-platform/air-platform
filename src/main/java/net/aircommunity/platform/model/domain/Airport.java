package net.aircommunity.platform.model.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import io.micro.annotation.constraint.NotEmpty;

/**
 * Airport information managed by ADMIN.
 * 
 * @author Bin.Zhang
 */
@Entity
//@formatter:off
@Table(name = "air_platform_airport", indexes = { 
	@Index(name = "idx_name_city", columnList = "name,city"),
	@Index(name = "idx_city", columnList = "city"),
	@Index(name = "idx_iata3", columnList = "iata3") ,
	@Index(name = "idx_icao4", columnList = "icao4", unique = true),
})
//@formatter:on
@XmlAccessorType(XmlAccessType.FIELD)
public class Airport extends Persistable {
	private static final long serialVersionUID = 1L;

	// airport name
	@NotEmpty
	@Size(max = AIRPORT_NAME_LEN)
	@Column(name = "name", length = AIRPORT_NAME_LEN, nullable = false)
	private String name;

	// of which country
	@NotEmpty
	@Size(max = COUNTRY_NAME_LEN)
	@Column(name = "country", length = COUNTRY_NAME_LEN, nullable = false)
	private String country;

	// of which city
	@NotEmpty
	@Size(max = CITY_NAME_LEN)
	@Column(name = "city", length = CITY_NAME_LEN, nullable = false)
	private String city;

	// IATA 3-Letter Code
	@Size(max = IATA3_LEN)
	@Column(name = "iata3", length = IATA3_LEN)
	private String iata3;

	// ICAO 4-Letter Code
	@NotEmpty
	@Size(max = ICAO4_LEN)
	@Column(name = "icao4", length = ICAO4_LEN, nullable = false, unique = true)
	private String icao4;

	// Decimal degrees, usually to six significant digits. Negative is South, positive is North.
	@Column(name = "lat", precision = 10, scale = 8)
	private BigDecimal latitude = BigDecimal.ZERO;

	// Decimal degrees, usually to six significant digits. Negative is West, positive is East.
	@Column(name = "lon", precision = 11, scale = 8)
	private BigDecimal longitude = BigDecimal.ZERO;

	// Hours offset from UTC. Fractional hours are expressed as decimals, eg. India is 5.5.
	// current max time zone string length is: 32, just make it a bit longer
	@Size(max = TIMEZONE_LEN)
	@Column(name = "timezone", length = TIMEZONE_LEN)
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

	public BigDecimal getLatitude() {
		return latitude;
	}

	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}

	public BigDecimal getLongitude() {
		return longitude;
	}

	public void setLongitude(BigDecimal longitude) {
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
