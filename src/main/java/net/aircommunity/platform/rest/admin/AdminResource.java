package net.aircommunity.platform.rest.admin;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micro.annotation.RESTful;
import io.micro.core.security.AccessTokenService;
import io.swagger.annotations.Api;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.rest.AirJetResource;
import net.aircommunity.platform.rest.AirportResource;
import net.aircommunity.platform.rest.BannerResource;
import net.aircommunity.platform.rest.CommentResource;
import net.aircommunity.platform.rest.PromotionResource;
import net.aircommunity.platform.rest.admin.order.AdminOrderResource;
import net.aircommunity.platform.rest.admin.product.AdminProductResource;
import net.aircommunity.platform.rest.tenant.TenantAirTaxiResource;
import net.aircommunity.platform.rest.tenant.TenantAirTourResourse;
import net.aircommunity.platform.rest.tenant.TenantAirTransportResource;
import net.aircommunity.platform.rest.tenant.TenantAircraftResource;
import net.aircommunity.platform.rest.tenant.TenantCourseResource;
import net.aircommunity.platform.rest.tenant.TenantFerryFlightResource;
import net.aircommunity.platform.rest.tenant.TenantFleetResource;
import net.aircommunity.platform.rest.tenant.TenantJetTravelResource;
import net.aircommunity.platform.rest.tenant.TenantProductFamilyResource;
import net.aircommunity.platform.rest.tenant.TenantSchoolResource;
import net.aircommunity.platform.rest.tenant.order.TenantAirTaxiOrderResource;
import net.aircommunity.platform.rest.tenant.order.TenantAirTourOrderResource;
import net.aircommunity.platform.rest.tenant.order.TenantAirTransportOrderResource;
import net.aircommunity.platform.rest.tenant.order.TenantEnrollmentResource;
import net.aircommunity.platform.rest.tenant.order.TenantFerryFlightOrderResource;
import net.aircommunity.platform.rest.tenant.order.TenantFleetOrderResource;
import net.aircommunity.platform.rest.tenant.order.TenantJetTravelOrderResource;
import net.aircommunity.platform.rest.user.AirTaxiOrderResource;
import net.aircommunity.platform.rest.user.AirTourOrderResource;
import net.aircommunity.platform.rest.user.AirTransportOrderResource;
import net.aircommunity.platform.rest.user.ChaterOrderResource;
import net.aircommunity.platform.rest.user.FerryFlightOrderResource;
import net.aircommunity.platform.rest.user.JetTravelOrderResource;
import net.aircommunity.platform.rest.user.UserEnrollmentResource;

/**
 * Admin RESTful API.
 * 
 * @author Bin.Zhang
 */
@Api
@RESTful
@Path("platform")
@RolesAllowed(Roles.ROLE_ADMIN)
public class AdminResource {
	private static final Logger LOG = LoggerFactory.getLogger(AdminResource.class);

	private static final String TENANTS_PATH_PREFIX = "tenants";
	private static final String USERS_PATH_PREFIX = "users";

	@Resource
	private AccessTokenService accessTokenService;

	/**
	 * Ping server to make sure server is still responsive, it is used for monitoring purpose.
	 */
	@GET
	@Path("ping")
	@PermitAll
	public void ping() {
		LOG.info("Got ping");
	}

	// ***********************
	// Platform Accounts
	// ***********************

	@Resource
	private AdminAccountResource adminAccountResource;

	@Path("accounts")
	public AdminAccountResource accounts() {
		return adminAccountResource;
	}

	// ***********************
	// Platform Generic
	// ***********************

	@Resource
	private AdminSettingsResource adminSettingsResource;

	@Path("settings")
	public AdminSettingsResource settings() {
		return adminSettingsResource;
	}

	@Resource
	private PromotionResource promotionResource;

	@Path("") // path already in the resource
	public PromotionResource promotions() {
		return promotionResource;
	}

	@Resource
	private BannerResource bannerResource;

	@Path("") // path already in the resource
	public BannerResource banners() {
		return bannerResource;
	}

	// ***********************
	// Product Management
	// ***********************

	@Resource
	private AirJetResource airJetResource;

	@Path("") // path already in the resource
	public AirJetResource airjets() {
		return airJetResource;
	}

	@Resource
	private AirportResource airportResource;

	@Path("") // path already in the resource
	public AirportResource airports() {
		return airportResource;
	}

	@Resource
	private AdminProductResource adminProductResource;

	@Path("") // path already in the resource
	public AdminProductResource products() {
		return adminProductResource;
	}

	// ***********************
	// Orders Management
	// ***********************

	@Resource
	private AdminOrderResource adminOrderResource;

	@Path("orders")
	public AdminOrderResource orders() {
		return adminOrderResource;
	}

	// ***********************
	// Comments
	// ***********************

	@Resource
	private CommentResource commentResource;

	@Path("") // path already in the resource
	public CommentResource comments() {
		return commentResource;
	}

	// **************************************************************************//
	// Tenant Products (XXX NOT USED FOR NOW)?
	// **************************************************************************//

	// ***********************
	// AirCraft information
	// ***********************
	@Resource
	private TenantAircraftResource tenantAircraftResource;

	@Path(TENANTS_PATH_PREFIX + "/{tenantId}/aircrafts")
	public TenantAircraftResource aircrafts(@PathParam("tenantId") String tenantId) {
		return tenantAircraftResource;
	}

	// ***********************
	// ProductFamily
	// ***********************

	@Resource
	private TenantProductFamilyResource tenantProductFamilyResource;

	@Path(TENANTS_PATH_PREFIX + "/{tenantId}/product/families")
	public TenantProductFamilyResource productFamilies(@PathParam("tenantId") String tenantId) {
		return tenantProductFamilyResource;
	}

	// ***********************
	// Air Jet
	// ***********************
	@Resource
	private TenantFleetResource tenantFleetResource;

	@Path(TENANTS_PATH_PREFIX + "/{tenantId}/fleets")
	public TenantFleetResource fleets(@PathParam("tenantId") String tenantId) {
		return tenantFleetResource;
	}

	@Resource
	private TenantFerryFlightResource tenantFerryFlightResource;

	@Path(TENANTS_PATH_PREFIX + "/{tenantId}/ferryflights")
	public TenantFerryFlightResource ferryflights(@PathParam("tenantId") String tenantId) {
		return tenantFerryFlightResource;
	}

	@Resource
	private TenantJetTravelResource tenantJetTravelResource;

	@Path(TENANTS_PATH_PREFIX + "/{tenantId}/jettravels")
	public TenantJetTravelResource jettravels(@PathParam("tenantId") String tenantId) {
		return tenantJetTravelResource;
	}

	// ***********************
	// Air Transport
	// ***********************
	@Resource
	private TenantAirTransportResource tenantAirTransportResource;

	@Path(TENANTS_PATH_PREFIX + "/{tenantId}/airtransports")
	public TenantAirTransportResource transports(@PathParam("tenantId") String tenantId) {
		return tenantAirTransportResource;
	}

	// ***********************
	// Air Taxi
	// ***********************
	@Resource
	private TenantAirTaxiResource tenantAirTaxiResource;

	@Path(TENANTS_PATH_PREFIX + "/{tenantId}/airtaxis")
	public TenantAirTaxiResource taxis(@PathParam("tenantId") String tenantId) {
		return tenantAirTaxiResource;
	}

	// ***********************
	// Air Tour
	// ***********************
	@Resource
	private TenantAirTourResourse tenantAirTourResourse;

	@Path(TENANTS_PATH_PREFIX + "/{tenantId}/airtours")
	public TenantAirTourResourse tours(@PathParam("tenantId") String tenantId) {
		return tenantAirTourResourse;
	}

	// ***********************
	// School/Course
	// ***********************
	@Resource
	private TenantSchoolResource tenantSchoolResource;

	@Path(TENANTS_PATH_PREFIX + "/{tenantId}/schools")
	public TenantSchoolResource schools(@PathParam("tenantId") String tenantId) {
		return tenantSchoolResource;
	}

	@Resource
	private TenantCourseResource tenantCourseResource;

	@Path(TENANTS_PATH_PREFIX + "/{tenantId}/courses")
	public TenantCourseResource courses(@PathParam("tenantId") String tenantId) {
		return tenantCourseResource;
	}

	// **************************************************************************//
	// Tenant orders (XXX NOT USED FOR NOW)?
	// **************************************************************************//

	// Air Jet
	@Resource
	private TenantFleetOrderResource tenantFleetOrderResource;

	@Path(TENANTS_PATH_PREFIX + "/{tenantId}/charter/orders")
	public TenantFleetOrderResource tenantCharterOrders(@PathParam("tenantId") String tenantId) {
		return tenantFleetOrderResource;
	}

	@Resource
	private TenantFerryFlightOrderResource tenantFerryFlightOrderResource;

	@Path(TENANTS_PATH_PREFIX + "/{tenantId}/ferryflight/orders")
	public TenantFerryFlightOrderResource tenantFerryFlightOrders(@PathParam("tenantId") String tenantId) {
		return tenantFerryFlightOrderResource;
	}

	@Resource
	private TenantJetTravelOrderResource tenantJetTravelOrderResource;

	@Path(TENANTS_PATH_PREFIX + "/{tenantId}/jettravel/orders")
	public TenantJetTravelOrderResource tenantJetTravelOrders(@PathParam("tenantId") String tenantId) {
		return tenantJetTravelOrderResource;
	}

	// Air Transport
	@Resource
	private TenantAirTransportOrderResource tenantAirTransportOrderResource;

	@Path(TENANTS_PATH_PREFIX + "/{tenantId}/airtransport/orders")
	public TenantAirTransportOrderResource tenantAirtransportOrders(@PathParam("tenantId") String tenantId) {
		return tenantAirTransportOrderResource;
	}

	// Air Taxi
	@Resource
	private TenantAirTaxiOrderResource tenantAirTaxiOrderResource;

	@Path(TENANTS_PATH_PREFIX + "/{tenantId}/airtaxi/orders")
	public TenantAirTaxiOrderResource tenantAirtaxiOrders(@PathParam("tenantId") String tenantId) {
		return tenantAirTaxiOrderResource;
	}

	// Air Tour
	@Resource
	private TenantAirTourOrderResource tenantAirTourOrderResource;

	@Path(TENANTS_PATH_PREFIX + "/{tenantId}/airtour/orders")
	public TenantAirTourOrderResource tenantAirtoursOrders(@PathParam("tenantId") String tenantId) {
		return tenantAirTourOrderResource;
	}

	@Resource
	private TenantEnrollmentResource tenantEnrollmentResource;

	@Path(TENANTS_PATH_PREFIX + "/{tenantId}/course/enrollments")
	public TenantEnrollmentResource enrollments(@PathParam("tenantId") String tenantId) {
		return tenantEnrollmentResource;
	}

	// **************************************************************************
	// User orders (XXX NOT USED FOR NOW)?
	// **************************************************************************

	// ***********************
	// Air Jet
	// ***********************
	@Resource
	private ChaterOrderResource chaterOrderResource;

	@Path(USERS_PATH_PREFIX + "/{userId}/chater/orders")
	public ChaterOrderResource chaterOrders(@PathParam("userId") String userId) {
		return chaterOrderResource;
	}

	@Resource
	private FerryFlightOrderResource ferryFlightOrderResource;

	@Path(USERS_PATH_PREFIX + "/{userId}/ferryflight/orders")
	public FerryFlightOrderResource ferryFlightOrders(@PathParam("userId") String userId) {
		return ferryFlightOrderResource;
	}

	@Resource
	private JetTravelOrderResource jetTravelOrderResource;

	@Path(USERS_PATH_PREFIX + "/{userId}/jettravel/orders")
	public JetTravelOrderResource jetTravelOrders(@PathParam("userId") String userId) {
		return jetTravelOrderResource;
	}

	// ***********************
	// Air Transport
	// ***********************
	@Resource
	private AirTransportOrderResource airTransportOrderResource;

	@Path(USERS_PATH_PREFIX + "/{userId}/airtransport/orders")
	public AirTransportOrderResource airTransportOrders(@PathParam("userId") String userId) {
		return airTransportOrderResource;
	}

	// ***********************
	// Air Taxi
	// ***********************
	@Resource
	private AirTaxiOrderResource airTaxiOrderResource;

	@Path(USERS_PATH_PREFIX + "/{userId}/airtaxi/orders")
	public AirTaxiOrderResource airTaxiOrders(@PathParam("userId") String userId) {
		return airTaxiOrderResource;
	}

	// ***********************
	// Air Tour
	// ***********************
	@Resource
	private AirTourOrderResource airTourOrderResource;

	@Path(USERS_PATH_PREFIX + "/{userId}/airtour/orders")
	public AirTourOrderResource airTourOrders(@PathParam("userId") String userId) {
		return airTourOrderResource;
	}

	// ***********************
	// Air Tour
	// ***********************
	@Resource
	private UserEnrollmentResource userEnrollmentResource;

	@Path(USERS_PATH_PREFIX + "/{userId}/course/enrollments")
	public UserEnrollmentResource userEnrollments(@PathParam("userId") String userId) {
		return userEnrollmentResource;
	}
}
