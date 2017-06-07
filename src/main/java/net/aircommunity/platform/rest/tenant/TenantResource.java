package net.aircommunity.platform.rest.tenant;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Path;

import io.micro.annotation.RESTful;
import io.swagger.annotations.Api;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.rest.CommentResource;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.rest.tenant.order.TenantAirTaxiOrderResource;
import net.aircommunity.platform.rest.tenant.order.TenantAirTourOrderResource;
import net.aircommunity.platform.rest.tenant.order.TenantAirTransportOrderResource;
import net.aircommunity.platform.rest.tenant.order.TenantEnrollmentResource;
import net.aircommunity.platform.rest.tenant.order.TenantFerryFlightOrderResource;
import net.aircommunity.platform.rest.tenant.order.TenantFleetOrderResource;
import net.aircommunity.platform.rest.tenant.order.TenantJetTravelOrderResource;

/**
 * Tenant resource.
 * 
 * @author Bin.Zhang
 */
@Api
@RESTful
@Path("tenant")
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT })
public class TenantResource {

	// ***********************
	// Air Jet
	// ***********************
	@Resource
	private TenantProductFamilyResource tenantProductFamilyResource;

	@Path("product/families")
	public TenantProductFamilyResource productFamilies() {
		return tenantProductFamilyResource;
	}

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
	private TenantJetTravelResource tenantJetTravelResource;

	@Path("jettravels")
	public TenantJetTravelResource jettravels() {
		return tenantJetTravelResource;
	}

	// ***********************
	// Air Transport
	// ***********************
	@Resource
	private TenantAirTransportResource tenantAirTransportResource;

	@Path("airtransports")
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
	// School
	// ***********************

	@Resource
	private TenantSchoolResource tenantSchoolResource;

	@Path("schools")
	public TenantSchoolResource schools() {
		return tenantSchoolResource;
	}

	@Resource
	private TenantCourseResource tenantCourseResource;

	@Path("courses")
	public TenantCourseResource courses() {
		return tenantCourseResource;
	}

	// ***********************
	// comments
	// ***********************

	@Resource
	private CommentResource commentResource;

	@Path("comments") // TODO --> @Path("") path already in resource ?
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
	private TenantJetTravelOrderResource tenantJetTravelOrderResource;

	@Path("jettravel/orders")
	public TenantJetTravelOrderResource jetTravelOrders() {
		return tenantJetTravelOrderResource;
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

	// ***********************
	// Course (enrollments)
	// ***********************

	@Resource
	private TenantEnrollmentResource tenantEnrollmentResource;

	@Path("course/enrollments")
	public TenantEnrollmentResource enrollments() {
		return tenantEnrollmentResource;
	}

	// TODO REMOVE
	// @Resource
	// private CommonOrderService commonOrderService;
	//
	// @POST
	// @Path("orders/{orderId}/finish")
	// public void finishOrder(@PathParam("orderId") String orderId) {
	// commonOrderService.updateOrderStatus(orderId, Order.Status.FINISHED);
	// }

}
