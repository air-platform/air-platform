package net.aircommunity.platform.model.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Geo Location.
 * 
 * @author Bin.Zhang
 */
@Embeddable
@XmlAccessorType(XmlAccessType.FIELD)
public class Location implements Serializable {
	private static final long serialVersionUID = 1L;

	// lat DECIMAL(10, 8), lng DECIMAL(11, 8)
	// Latitudes range from -90 to +90 (degrees), so DECIMAL(10, 8)
	// longitudes range from -180 to +180 (degrees) so you need DECIMAL(11, 8)
	@Column(name = "lat", precision = 10, scale = 8)
	private BigDecimal latitude = BigDecimal.ZERO;

	@Column(name = "lon", precision = 11, scale = 8)
	private BigDecimal longitude = BigDecimal.ZERO;

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
		builder.append("Location [latitude=").append(latitude).append(", longitude=").append(longitude).append("]");
		return builder.toString();
	}
}