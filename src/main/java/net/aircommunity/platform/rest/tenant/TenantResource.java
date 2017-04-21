package net.aircommunity.platform.rest.tenant;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import io.micro.annotation.RESTful;
import io.swagger.annotations.Api;
import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.rest.CommentResource;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.rest.tenant.order.TenantAirTaxiOrderResource;
import net.aircommunity.platform.rest.tenant.order.TenantAirTourOrderResource;
import net.aircommunity.platform.rest.tenant.order.TenantAirTransportOrderResource;
import net.aircommunity.platform.rest.tenant.order.TenantEnrollmentResource;
import net.aircommunity.platform.rest.tenant.order.TenantFerryFlightOrderResource;
import net.aircommunity.platform.rest.tenant.order.TenantFleetOrderResource;
import net.aircommunity.platform.rest.tenant.order.TenantJetCardOrderResource;
import net.aircommunity.platform.service.CommonOrderService;

/**
 * Tenant resource.
 * 
 * @author Bin.Zhang
 */
@Api
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
	// School TODO
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

	@Resource
	private TenantEnrollmentResource tenantEnrollmentResource;

	@Path("enrollments")
	public TenantEnrollmentResource enrollments() {
		return tenantEnrollmentResource;
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

	// *******//
	// Orders //
	// *******//
	@Resource
	private CommonOrderService commonOrderService;

	@POST
	@Path("orders/{orderId}/finish")
	@Produces(MediaType.APPLICATION_JSON)
	public Response finishOrder(@PathParam("orderId") String orderId, @Context SecurityContext context) {
		commonOrderService.updateOrderStatus(orderId, Order.Status.FINISHED);
		return Response.noContent().build();
	}

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
