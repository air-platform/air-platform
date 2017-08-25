package net.aircommunity.platform.model.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import io.micro.annotation.constraint.NotEmpty;

/**
 * Location information for QuickFlight.
 * 
 * @author Bin.Zhang
 */
@Embeddable
@XmlAccessorType(XmlAccessType.FIELD)
public class QuickFlightLocation implements DomainConstants, Serializable {
	private static final long serialVersionUID = 1L;

	// city
	@NotEmpty
	@Size(max = CITY_NAME_LEN)
	@Column(name = "city", length = CITY_NAME_LEN, nullable = false)
	private String city;

	// location name
	@NotEmpty
	@Size(max = CITY_NAME_LEN)
	@Column(name = "name", length = CITY_NAME_LEN, nullable = false)
	private String name;

	// location address
	@NotEmpty
	@Size(max = ADDRESS_LEN)
	@Column(name = "address", length = ADDRESS_LEN, nullable = false)
	private String address;

	// lat DECIMAL(10, 8), lng DECIMAL(11, 8)
	// Latitudes range from -90 to +90 (degrees), so DECIMAL(10, 8)
	// longitudes range from -180 to +180 (degrees) so you need DECIMAL(11, 8)
	@Column(name = "lat", precision = 10, scale = 8)
	private BigDecimal latitude = BigDecimal.ZERO;

	@Column(name = "lon", precision = 11, scale = 8)
	private BigDecimal longitude = BigDecimal.ZERO;

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("QuickFlightLocation [city=").append(city).append(", name=").append(name).append(", address=")
				.append(address).append(", latitude=").append(latitude).append(", longitude=").append(longitude)
				.append("]");
		return builder.toString();
	}

}
