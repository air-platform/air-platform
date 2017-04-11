package net.aircommunity.platform.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Charter Order on a {@code Fleet}.
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platfrom_charter_order")
public class CharterOrder extends Order {
	private static final long serialVersionUID = 1L;

	@Column(name = "aircraft_type")
	private String aircraftType;

	// multiple flight legs
	private Set<FlightLeg> flightLegs = new HashSet<>();

	@ManyToOne
	@JoinColumn(name = "fleet_id", nullable = false)
	private Fleet fleet;

}
