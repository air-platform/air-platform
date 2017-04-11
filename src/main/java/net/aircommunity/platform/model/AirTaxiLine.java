package net.aircommunity.platform.model;

import javax.persistence.Column;
import javax.persistence.Lob;

public class AirTaxiLine extends Persistable {
	private static final long serialVersionUID = 1L;

	// the line name
	@Column(name = "name", nullable = false)
	private String name;

	// in which city
	@Column(name = "city", nullable = false)
	private String city;

	// from/to
	// fromLocation/toLocation

	// card description, e.g. hour card: 25hours
	@Lob
	@Column(name = "description")
	private String description;
}
