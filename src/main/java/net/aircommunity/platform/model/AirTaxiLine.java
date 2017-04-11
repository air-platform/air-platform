package net.aircommunity.platform.model;

import javax.persistence.Column;

public class AirTaxiLine extends Product {
	private static final long serialVersionUID = 1L;

	// properties inherited:
	// name: e.g. air line name
	// price
	// desc

	// in which city
	@Column(name = "city", nullable = false)
	private String city;

	// from/to
	// fromLocation/toLocation

}
