package net.aircommunity.platform.rest.tenant;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import io.micro.annotation.RESTful;
import io.swagger.annotations.Api;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.model.domain.Product.Category;
import net.aircommunity.platform.model.metrics.OrderMetrics;
import net.aircommunity.platform.model.metrics.ProductMetrics;
import net.aircommunity.platform.model.metrics.TradeMetrics;
import net.aircommunity.platform.rest.BaseResourceSupport;
import net.aircommunity.platform.rest.CommentResource;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.rest.tenant.order.TenantAirTaxiOrderResource;
import net.aircommunity.platform.rest.tenant.order.TenantAirTourOrderResource;
import net.aircommunity.platform.rest.tenant.order.TenantAirTransportOrderResource;
import net.aircommunity.platform.rest.tenant.order.TenantCharterOrderResource;
import net.aircommunity.platform.rest.tenant.order.TenantCourseOrderResource;
import net.aircommunity.platform.rest.tenant.order.TenantFerryFlightOrderResource;
import net.aircommunity.platform.rest.tenant.order.TenantJetTravelOrderResource;
import net.aircommunity.platform.rest.tenant.order.TenantQuickFlightOrderResource;

/**
 * Tenant resource.
 * 
 * @author Bin.Zhang
 */
@Api
@RESTful
@Path("tenant")
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT, Roles.ROLE_CUSTOMER_SERVICE })
public class TenantResource extends BaseResourceSupport {

	// ***********************
	// Metrics
	// ***********************
	@GET
	@Path("product/metrics")
	@Produces(MediaType.APPLICATION_JSON)
	public ProductMetrics productMetrics(@Context SecurityContext context) {
		String tenantId = context.getUserPrincipal().getName();
		return commonProductService.getProductMetrics(tenantId);
	}

	@GET
	@Path("order/metrics")
	@Produces(MediaType.APPLICATION_JSON)
	public OrderMetrics orderMetrics(@Context SecurityContext context) {
		String tenantId = context.getUserPrincipal().getName();
		return commonOrderService.getOrderMetrics(tenantId);
	}

	@GET
	@Path("trade/metrics")
	@Produces(MediaType.APPLICATION_JSON)
	public TradeMetrics tradeMetrics(@Context SecurityContext context) {
		String tenantId = context.getUserPrincipal().getName();
		return paymentService.getTradeMetrics(tenantId);
	}

	// ***********************
	// Air Jet
	// ***********************
	@Resource
	private TenantProductFamilyResource tenantProductFamilyResource;

	@Path("product/families")
	public TenantProductFamilyResource productFamilies() {
		return tenantProductFamilyResource;
	}

	@GET
	@Path("product/categories")
	@Produces(MediaType.APPLICATION_JSON)
	public Category[] productCategories() {
		return Product.Category.values();
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
	// Quick Flight
	// ***********************
	@Resource
	private TenantQuickFlightOrderResource tenantQuickFlightOrderResource;

	@Path("quickflight/orders")
	public TenantQuickFlightOrderResource quickFlightOrders() {
		return tenantQuickFlightOrderResource;
	}

	// ***********************
	// Air Jet
	// ***********************
	@Resource
	private TenantCharterOrderResource tenantCharterOrderResource;

	@Path("charter/orders")
	public TenantCharterOrderResource charterOrders() {
		return tenantCharterOrderResource;
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
	private TenantCourseOrderResource tenantCourseOrderResource;

	@Path("course/orders")
	public TenantCourseOrderResource courseOrders() {
		return tenantCourseOrderResource;
	}

}
