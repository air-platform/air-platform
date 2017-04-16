package net.aircommunity.platform.rest.tenant;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Path;

import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.rest.CommentResource;
import net.aircommunity.platform.rest.SchoolResource;
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

	// ***********************
	// Air Jet
	// ***********************
	@Resource
	private TenantFleetResource tenantFleetResource;

	@Path("fleets")
	public TenantFleetResource fleets() {
		return tenantFleetResource;
	}

	@Resource
	private TenantFerryFlightResource tenantFerryFlightResource;

	@Path("ferryflights")
	public TenantFerryFlightResource ferryflights() {
		return tenantFerryFlightResource;
	}

	@Resource
	private TenantJetCardResource tenantJetCardResource;

	@Path("jetcards")
	public TenantJetCardResource jetcards() {
		return tenantJetCardResource;
	}

	// ***********************
	// Air Transport
	// ***********************
	@Resource
	private TenantAirTransportResource tenantAirTransportResource;

	@Path("airTransports")
	public TenantAirTransportResource airTransports() {
		return tenantAirTransportResource;
	}

	// ***********************
	// Air Taxi
	// ***********************
	@Resource
	private TenantAirTaxiResource tenantAirTaxiResource;

	@Path("airtaxis")
	public TenantAirTaxiResource airtaxis() {
		return tenantAirTaxiResource;
	}

	// ***********************
	// Air Tour
	// ***********************
	@Resource
	private TenantAirTourResourse tenantAirTourResourse;

	@Path("airtours")
	public TenantAirTourResourse airTours() {
		return tenantAirTourResourse;
	}

	// ***********************
	// AirCraft information
	// ***********************
	@Resource
	private TenantAircraftResource tenantAircraftResource;

	@Path("aircrafts")
	public TenantAircraftResource aircrafts() {
		return tenantAircraftResource;
	}

	// ***********************
	// School TODO
	// ***********************

	@Resource
	private SchoolResource schoolResource;

	@Path("schools")
	public SchoolResource schools() {
		return schoolResource;
	}

	// ***********************
	// comments TODO
	// ***********************

	@Resource
	private CommentResource commentResource;

	@Path("comments")
	public CommentResource comments() {
		return commentResource;
	}

	// *******//
	// Orders //
	// *******//

	// ***********************
	// Air Jet
	// ***********************
	@Resource
	private TenantFleetOrderResource tenantFleetOrderResource;

	@Path("charter/orders")
	public TenantFleetOrderResource fleetOrders() {
		return tenantFleetOrderResource;
	}

	@Resource
	private TenantFerryFlightOrderResource tenantFerryFlightOrderResource;

	@Path("ferryflight/orders")
	public TenantFerryFlightOrderResource ferryFlightOrders() {
		return tenantFerryFlightOrderResource;
	}

	@Resource
	private TenantJetCardOrderResource tenantJetCardOrderResource;

	@Path("jetcard/orders")
	public TenantJetCardOrderResource jetCardOrders() {
		return tenantJetCardOrderResource;
	}

	// ***********************
	// Air Transport
	// ***********************
	@Resource
	private TenantAirTransportOrderResource tenantAirTransportOrderResource;

	@Path("airtransport/orders")
	public TenantAirTransportOrderResource airtransportOrders() {
		return tenantAirTransportOrderResource;
	}

	// ***********************
	// Air Taxi
	// ***********************
	@Resource
	private TenantAirTaxiOrderResource tenantAirTaxiOrderResource;

	@Path("airtaxi/orders")
	public TenantAirTaxiOrderResource airtaxiOrders() {
		return tenantAirTaxiOrderResource;
	}

	// ***********************
	// Air Tour
	// ***********************
	@Resource
	private TenantAirTourOrderResource tenantAirTourOrderResource;

	@Path("airtour/orders")
	public TenantAirTourOrderResource airtoursOrders() {
		return tenantAirTourOrderResource;
	}

}
