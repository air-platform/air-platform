package net.aircommunity.platform.model;

import java.io.Serializable;

/**
 * Geo Location.
 * 
 * @author Bin.Zhang
 */
public class Location implements Serializable {
	private static final long serialVersionUID = 1L;

	private double latitude;
	private double longitude;

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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Location [latitude=").append(latitude).append(", longitude=").append(longitude).append("]");
		return builder.toString();
	}
}