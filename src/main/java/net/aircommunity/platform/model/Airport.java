package net.aircommunity.platform.model;

public class Airport extends Persistable {
	private static final long serialVersionUID = 1L;

	// airport name
	private String name;

	// of which city
	private String city;

	// of which country
	private String country;

	// IATA 3-Letter Code
	private String iata3;

	// ICAO 4-Letter Code
	private String icao4;

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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Airport [name=").append(name).append(", city=").append(city).append(", country=")
				.append(country).append(", iata3=").append(iata3).append(", icao4=").append(icao4).append(", id=")
				.append(id).append("]");
		return builder.toString();
	}
}
