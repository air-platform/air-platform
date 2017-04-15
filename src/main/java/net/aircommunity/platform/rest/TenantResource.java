package net.aircommunity.platform.rest;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Path;

import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.rest.annotation.RESTful;

/**
 * Tenant resource.
 * 
 * @author Bin.Zhang
 */
@RESTful
@Path("tenant")
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT })
@AllowResourceOwner
public class TenantResource {

	@Resource
	private FleetResource fleetResource;

	@Resource
	private FerryFlightResource ferryFlightResource;

	@Resource
	private JetCardResource jetCardResource;

	// ***********************
	// Air Jet
	// ***********************

	@Path("fleets")
	public FleetResource fleets() {
		return fleetResource;
	}

	@Path("ferryflights")
	public FerryFlightResource ferryflights() {
		return ferryFlightResource;
	}

	@Path("jetcards")
	public JetCardResource jetcards() {
		return jetCardResource;
	}

	// aircraft
	@Resource
	private AircraftResource aircraftResource;

	@Path("aircrafts")
	public AircraftResource aircrafts() {
		return aircraftResource;
	}

	// transports
	@Resource
	private AirTransportResource airTransportResource;

	@Path("transports")
	public AirTransportResource transports() {
		return airTransportResource;
	}

	// ***********************
	// School
	// ***********************

	@Resource
	private SchoolResource schoolResource;

	@Path("schools")
	public SchoolResource schools() {
		return schoolResource;
	}

	// ***********************
	// comments
	// ***********************

	@Resource
	private CommentResource commentResource;

	@Path("comments")
	public CommentResource comments() {
		return commentResource;
	}

}
