package net.aircommunity.platform.rest.admin;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micro.annotation.RESTful;
import io.micro.common.Strings;
import io.micro.core.security.AccessTokenService;
import io.swagger.annotations.Api;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.ProductSummary;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.model.domain.Product.Category;
import net.aircommunity.platform.model.domain.Reviewable.ReviewStatus;
import net.aircommunity.platform.rest.AirClassResource;
import net.aircommunity.platform.rest.AirJetResource;
import net.aircommunity.platform.rest.AirportResource;
import net.aircommunity.platform.rest.BannerResource;
import net.aircommunity.platform.rest.BaseResourceSupport;
import net.aircommunity.platform.rest.CommentResource;
import net.aircommunity.platform.rest.PromotionResource;
import net.aircommunity.platform.rest.admin.order.AdminOrderResource;
import net.aircommunity.platform.rest.admin.product.AdminAirTaxiResource;
import net.aircommunity.platform.rest.admin.product.AdminAirTourResourse;
import net.aircommunity.platform.rest.admin.product.AdminAirTransportResource;
import net.aircommunity.platform.rest.admin.product.AdminAircraftResource;
import net.aircommunity.platform.rest.admin.product.AdminCourseResource;
import net.aircommunity.platform.rest.admin.product.AdminFerryFlightResource;
import net.aircommunity.platform.rest.admin.product.AdminFleetResource;
import net.aircommunity.platform.rest.admin.product.AdminJetTravelResource;
import net.aircommunity.platform.rest.admin.product.AdminProductFamilyResource;
import net.aircommunity.platform.rest.admin.product.AdminSchoolResource;
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
import net.aircommunity.platform.rest.tenant.order.TenantCharterOrderResource;
import net.aircommunity.platform.rest.tenant.order.TenantCourseOrderResource;
import net.aircommunity.platform.rest.tenant.order.TenantFerryFlightOrderResource;
import net.aircommunity.platform.rest.tenant.order.TenantJetTravelOrderResource;
import net.aircommunity.platform.rest.user.AirTaxiOrderResource;
import net.aircommunity.platform.rest.user.AirTourOrderResource;
import net.aircommunity.platform.rest.user.AirTransportOrderResource;
import net.aircommunity.platform.rest.user.CharterOrderResource;
import net.aircommunity.platform.rest.user.FerryFlightOrderResource;
import net.aircommunity.platform.rest.user.JetTravelOrderResource;
import net.aircommunity.platform.rest.user.UserCourseOrderResource;
import net.aircommunity.platform.service.PlatformService;

/**
 * ADMIN RESTful API.
 * 
 * @author Bin.Zhang
 */
@Api
@RESTful
@Path("platform")
@RolesAllowed(Roles.ROLE_ADMIN)
public class AdminResource extends BaseResourceSupport {
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
	private PlatformService platformService;

	@POST
	@Path("caches/clear")
	public void clearAllCaches(@QueryParam("cache") String cacheName) {
		if (Strings.isBlank(cacheName)) {
			platformService.clearAllCaches();
		}
		else {
			platformService.clearCache(cacheName);
		}
	}

	@Resource
	private AdminSettingsResource adminSettingsResource;

	@Path("settings")
	public AdminSettingsResource settings() {
		return adminSettingsResource;
	}

	@Resource
	private PromotionResource promotionResource;

	@Path("promotions")
	public PromotionResource promotions() {
		return promotionResource;
	}

	@Resource
	private BannerResource bannerResource;

	@Path("banners")
	public BannerResource banners() {
		return bannerResource;
	}

	// ***********************
	// Product Management
	// ***********************

	@Resource
	private AirJetResource airJetResource;

	@Path("airjets")
	public AirJetResource airjets() {
		return airJetResource;
	}

	@Resource
	private AirClassResource airClassResource;

	@Path("airclasses")
	public AirClassResource airclasses() {
		return airClassResource;
	}

	@Resource
	private AirportResource airportResource;

	@Path("airports")
	public AirportResource airports() {
		return airportResource;
	}

	// TODO REMOVE
	// @Resource
	// private AdminProductResource adminProductResource;
	//
	// @Path("")
	// public AdminProductResource products() {
	// return adminProductResource;
	// }

	// ***********************
	// Product Management
	// ***********************

	// *********
	// SCHOOL
	// *********
	@Resource
	private AdminSchoolResource adminSchoolResource;

	@Path("schools")
	public AdminSchoolResource schools() {
		return adminSchoolResource;
	}

	// *********
	// AIRCRAFT
	// *********
	@Resource
	private AdminAircraftResource adminAircraftResource;

	@Path("aircrafts")
	public AdminAircraftResource aircrafts() {
		return adminAircraftResource;
	}

	// *********
	// FAMILY
	// *********
	@Resource
	private AdminProductFamilyResource adminProductFamilyResource;

	@Path("product/families")
	public AdminProductFamilyResource families() {
		return adminProductFamilyResource;
	}

	@GET
	@Path("product/categories")
	@Produces(MediaType.APPLICATION_JSON)
	public Category[] productCategories() {
		return Product.Category.values();
	}

	// *********
	// JETTRAVEL
	// *********
	@Resource
	private AdminJetTravelResource adminJetTravelResource;

	@Path("product/jettravels")
	public AdminJetTravelResource jettravels() {
		return adminJetTravelResource;
	}

	// *********
	// TAXI
	// *********
	@Resource
	private AdminAirTaxiResource adminAirTaxiResource;

	@Path("product/airtaxis")
	public AdminAirTaxiResource taxis() {
		return adminAirTaxiResource;
	}

	// *********
	// TOUR
	// *********
	@Resource
	private AdminAirTourResourse adminAirTourResourse;

	@Path("product/airtours")
	public AdminAirTourResourse tours() {
		return adminAirTourResourse;
	}

	// *********
	// TRANPORT
	// *********
	@Resource
	private AdminAirTransportResource adminAirTransportResource;

	@Path("product/airtransports")
	public AdminAirTransportResource tranports() {
		return adminAirTransportResource;
	}

	// ************
	// FERRYFLIGHT
	// ************
	@Resource
	private AdminFerryFlightResource adminFerryFlightResource;

	@Path("product/ferryflights")
	public AdminFerryFlightResource ferryflights() {
		return adminFerryFlightResource;
	}

	// *********
	// FLEET
	// *********
	@Resource
	private AdminFleetResource adminFleetResource;

	@Path("product/fleets")
	public AdminFleetResource fleets() {
		return adminFleetResource;
	}

	// *********
	// COURSE
	// *********
	@Resource
	private AdminCourseResource adminCourseResource;

	@Path("product/courses")
	public AdminCourseResource courses() {
		return adminCourseResource;
	}

	@GET
	@Path("product/summaries")
	@Produces(MediaType.APPLICATION_JSON)
	public Response productSummaries(@QueryParam("category") Category category,
			@QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		Page<Product> result = commonProductService.listAllApprovedProducts(category, page, pageSize);
		List<ProductSummary> content = result.getContent().stream().map(ProductSummary::new)
				.collect(Collectors.toList());
		if (page > 0) {
			return Response.ok(
					new Page<ProductSummary>(result.getPage(), result.getPageSize(), result.getTotalRecords(), content))
					.build();
		}
		return Response.ok(content).build();
	}

	// **************
	// Review Product
	// **************
	@POST
	@Path("products/{productId}")
	public Product findProduct(@PathParam("productId") String productId) {
		return commonProductService.findProduct(productId);
	}

	/**
	 * Approve a tenant product
	 */
	@POST
	@Path("products/{productId}/approve")
	public void approveProduct(@PathParam("productId") String productId) {
		commonProductService.reviewProduct(productId, ReviewStatus.APPROVED, null);
	}

	/**
	 * Disapprove a tenant product (body: {"reason": "xxxx"} )
	 */
	@POST
	@Path("products/{productId}/disapprove")
	@Consumes(MediaType.APPLICATION_JSON)
	public void disapproveProduct(@PathParam("productId") String productId, @NotNull JsonObject rejectedReason) {
		String reason = getReason(rejectedReason);
		commonProductService.reviewProduct(productId, ReviewStatus.REJECTED, reason);
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

	@Path("comments") // path already in the resource
	public CommentResource comments() {
		return commentResource;
	}

	// **************************************************************************//
	// Tenant Products (XXX NOT USED FOR NOW, it may be useful in future?)
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
	// Tenant orders (XXX NOT USED FOR NOW, it may be useful in future?)
	// **************************************************************************//

	// Air Jet
	@Resource
	private TenantCharterOrderResource tenantFleetOrderResource;

	@Path(TENANTS_PATH_PREFIX + "/{tenantId}/charter/orders")
	public TenantCharterOrderResource tenantCharterOrders(@PathParam("tenantId") String tenantId) {
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
	private TenantCourseOrderResource tenantCourseOrderResource;

	@Path(TENANTS_PATH_PREFIX + "/{tenantId}/course/orders")
	public TenantCourseOrderResource courseOrders(@PathParam("tenantId") String tenantId) {
		return tenantCourseOrderResource;
	}

	// **************************************************************************
	// User orders (XXX NOT USED FOR NOW, it may be useful in future?)
	// **************************************************************************

	// ***********************
	// Air Jet
	// ***********************
	@Resource
	private CharterOrderResource chaterOrderResource;

	@Path(USERS_PATH_PREFIX + "/{userId}/chater/orders")
	public CharterOrderResource chaterOrders(@PathParam("userId") String userId) {
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
	private UserCourseOrderResource userCourseOrderResource;

	@Path(USERS_PATH_PREFIX + "/{userId}/course/orders")
	public UserCourseOrderResource userCourseOrders(@PathParam("userId") String userId) {
		return userCourseOrderResource;
	}
}
