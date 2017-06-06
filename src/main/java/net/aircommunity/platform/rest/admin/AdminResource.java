package net.aircommunity.platform.rest.admin;

import java.net.URI;
import java.util.Base64;
import java.util.Set;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonView;

import io.micro.annotation.RESTful;
import io.micro.core.security.AccessTokenService;
import io.swagger.annotations.Api;
import net.aircommunity.platform.Constants;
import net.aircommunity.platform.model.AccessToken;
import net.aircommunity.platform.model.Account;
import net.aircommunity.platform.model.AccountRequest;
import net.aircommunity.platform.model.Contact;
import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Product.Category;
import net.aircommunity.platform.model.ProductFamily;
import net.aircommunity.platform.model.Role;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.Tenant.VerificationStatus;
import net.aircommunity.platform.rest.AirJetResource;
import net.aircommunity.platform.rest.AirportResource;
import net.aircommunity.platform.rest.CommentResource;
import net.aircommunity.platform.rest.PromotionResource;
import net.aircommunity.platform.rest.tenant.TenantAirTaxiResource;
import net.aircommunity.platform.rest.tenant.TenantAirTourResourse;
import net.aircommunity.platform.rest.tenant.TenantAirTransportResource;
import net.aircommunity.platform.rest.tenant.TenantAircraftResource;
import net.aircommunity.platform.rest.tenant.TenantCourseResource;
import net.aircommunity.platform.rest.tenant.TenantFerryFlightResource;
import net.aircommunity.platform.rest.tenant.TenantFleetResource;
import net.aircommunity.platform.rest.tenant.TenantJetCardResource;
import net.aircommunity.platform.rest.tenant.TenantProductFamilyResource;
import net.aircommunity.platform.rest.tenant.TenantSchoolResource;
import net.aircommunity.platform.rest.tenant.order.TenantAirTaxiOrderResource;
import net.aircommunity.platform.rest.tenant.order.TenantAirTourOrderResource;
import net.aircommunity.platform.rest.tenant.order.TenantAirTransportOrderResource;
import net.aircommunity.platform.rest.tenant.order.TenantEnrollmentResource;
import net.aircommunity.platform.rest.tenant.order.TenantFerryFlightOrderResource;
import net.aircommunity.platform.rest.tenant.order.TenantFleetOrderResource;
import net.aircommunity.platform.rest.tenant.order.TenantJetCardOrderResource;
import net.aircommunity.platform.rest.user.AirTaxiOrderResource;
import net.aircommunity.platform.rest.user.AirTourOrderResource;
import net.aircommunity.platform.rest.user.AirTransportOrderResource;
import net.aircommunity.platform.rest.user.ChaterOrderResource;
import net.aircommunity.platform.rest.user.FerryFlightOrderResource;
import net.aircommunity.platform.rest.user.JetcardOrderResource;
import net.aircommunity.platform.rest.user.UserEnrollmentResource;
import net.aircommunity.platform.service.AccountService;
import net.aircommunity.platform.service.CommonOrderService;
import net.aircommunity.platform.service.PlatformService;
import net.aircommunity.platform.service.ProductFamilyService;

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

	private static final String ACCOUNTS_PATH_PREFIX = "accounts";
	private static final String TENANTS_PATH_PREFIX = "tenants";
	private static final String USERS_PATH_PREFIX = "users";

	@Resource
	private PlatformService platformService;

	@Resource
	private AccountService accountService;

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

	// **************************************
	// Platform generic settings
	// **************************************

	/**
	 * set platform client managers
	 */
	@PUT
	@Path("settings")
	@Consumes(MediaType.APPLICATION_JSON)
	public void setPlatformClientManagers(@NotNull @Valid Set<Contact> clientManagers) {
		platformService.setPlatformClientManagers(clientManagers);
	}

	/**
	 * set platform client managers
	 */
	@GET
	@Path("settings")
	@Produces(MediaType.APPLICATION_JSON)
	public Set<Contact> getPlatformClientManagers() {
		return platformService.getPlatformClientManagers();
	}

	// **************************************
	// Account administration
	// **************************************

	/**
	 * Get access token Secret
	 */
	@GET
	@Path("accesstoken/secret")
	@Consumes(MediaType.APPLICATION_JSON)
	public AccessToken getAccessTokenPublicKey() {
		String key = Base64.getEncoder().encodeToString(accessTokenService.getPublicKey().getEncoded());
		return new AccessToken(key);
	}

	/**
	 * Create a tenant account
	 */
	@POST
	@Path(ACCOUNTS_PATH_PREFIX)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createAccount(@NotNull @Valid AccountRequest request, @Context UriInfo uriInfo) {
		Account created = accountService.createAccount(request.getUsername(), request.getPassword(), request.getRole());
		URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
		LOG.debug("Created account: {}", uri);
		return Response.created(uri).build();
	}

	/**
	 * List All accounts
	 */
	@GET
	@Path(ACCOUNTS_PATH_PREFIX)
	@Produces(MediaType.APPLICATION_JSON)
	public Page<Account> listAllAccounts(@QueryParam("role") Role role, @QueryParam("page") @DefaultValue("0") int page,
			@QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		return accountService.listAccounts(role, page, pageSize);
	}

	/**
	 * Gets a account
	 */
	@GET
	@Path(ACCOUNTS_PATH_PREFIX + "/{accountId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Account findAccount(@PathParam("accountId") String accountId) {
		return accountService.findAccount(accountId);
	}

	/**
	 * Reset password for a account
	 */
	@POST
	@Path(ACCOUNTS_PATH_PREFIX + "/{accountId}/password/reset")
	public void resetAccountPassword(@PathParam("accountId") String accountId) {
		accountService.resetPasswordViaEmail(accountId);
	}

	/**
	 * Reset password for a account to default password
	 */
	@POST
	@Path(ACCOUNTS_PATH_PREFIX + "/{accountId}/password/reset/default")
	public void resetAccountPasswordTo(@PathParam("accountId") String accountId) {
		accountService.resetPasswordTo(accountId, Constants.DEFAULT_PASSWORD);
	}

	/**
	 * Lock a account
	 */
	@POST
	@Path(ACCOUNTS_PATH_PREFIX + "/{accountId}/lock")
	public void lockAccount(@PathParam("accountId") String accountId) {
		accountService.updateAccountStatus(accountId, Account.Status.DISABLED);
	}

	/**
	 * Unlock a account
	 */
	@POST
	@Path(ACCOUNTS_PATH_PREFIX + "/{accountId}/unlock")
	public void unlockAccount(@PathParam("accountId") String accountId) {
		accountService.updateAccountStatus(accountId, Account.Status.ENABLED);
	}

	/**
	 * Verify a tenant account
	 */
	@POST
	@Path(ACCOUNTS_PATH_PREFIX + "/{accountId}/verified")
	public void verifyTenantAccount(@PathParam("accountId") String accountId) {
		accountService.updateTenantVerificationStatus(accountId, VerificationStatus.VERIFIED);
	}

	/**
	 * Unverify a tenant account
	 */
	@POST
	@Path(ACCOUNTS_PATH_PREFIX + "/{accountId}/unverified")
	public void unverifyTenantAccount(@PathParam("accountId") String accountId) {
		accountService.updateTenantVerificationStatus(accountId, VerificationStatus.UNVERIFIED);
	}

	/**
	 * Delete a account
	 */
	@Path(ACCOUNTS_PATH_PREFIX + "/{accountId}")
	@DELETE
	public void deleteAccount(@PathParam("accountId") String accountId) {
		accountService.deleteAccount(accountId);
	}

	// *************
	// Common
	// *************

	// ***********************
	// Product review
	// ***********************

	@Resource
	private AdminProductResource adminProductResource;

	@Path("product")
	public AdminProductResource product() {
		return adminProductResource;
	}

	// ***********************
	// Promotion
	// ***********************
	@Resource
	private PromotionResource promotionResource;

	@Path("")
	public PromotionResource promotions() {
		return promotionResource;
	}

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

	// ***********************
	// Generic Orders Operations
	// ***********************
	@Resource
	private CommonOrderService commonOrderService;

	@GET
	@Path("orders")
	@Produces(MediaType.APPLICATION_JSON)
	public Page<Order> listAllOrders(@QueryParam("user") String userId, @QueryParam("status") Order.Status status,
			@QueryParam("page") @DefaultValue("1") int page, @QueryParam("pageSize") @DefaultValue("10") int pageSize) {
		return commonOrderService.listAllUserOrders(userId, status, page, pageSize);
	}

	@GET
	@Path("orders/{orderId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Order findOrder(@PathParam("orderId") String orderId) {
		return commonOrderService.findOrder(orderId);
	}

	@POST
	@Path("orders/{orderId}/status")
	@Produces(MediaType.APPLICATION_JSON)
	public void updateOrderStatus(@PathParam("orderId") String orderId,
			@NotNull @QueryParam("status") Order.Status status) {
		commonOrderService.updateOrderStatus(orderId, status);
	}

	@DELETE
	@Path("orders/{orderId}")
	@Produces(MediaType.APPLICATION_JSON)
	public void deleteOrder(@PathParam("orderId") String orderId) {
		commonOrderService.deleteOrder(orderId);
	}

	// ***********************
	// Comments
	// ***********************

	@Resource
	private CommentResource commentResource;

	@Path("comments")
	public CommentResource comments() {
		return commentResource;
	}

	// *************
	// Tenant
	// *************

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
	private TenantJetCardResource tenantJetCardResource;

	@Path(TENANTS_PATH_PREFIX + "/{tenantId}/jetcards")
	public TenantJetCardResource jetcards(@PathParam("tenantId") String tenantId) {
		return tenantJetCardResource;
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
	private ProductFamilyService productFamilyService;

	/**
	 * List all
	 */
	@GET
	@Path("product/families")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Admin.class)
	public Page<ProductFamily> listAllProductFamilies(@QueryParam("category") Category category,
			@QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		LOG.debug("List all families with category: {}, page: {}, pageSize: {}", category, page, pageSize);
		if (category != null) {
			return productFamilyService.listProductFamiliesByCategory(category, page, pageSize);
		}
		return productFamilyService.listProductFamilies(page, pageSize);
	}

	@Resource
	private TenantProductFamilyResource tenantProductFamilyResource;

	@Path(TENANTS_PATH_PREFIX + "/{tenantId}/product/families")
	public TenantProductFamilyResource productFamilies(@PathParam("tenantId") String tenantId) {
		return tenantProductFamilyResource;
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

	@Resource
	private TenantEnrollmentResource tenantEnrollmentResource;

	@Path(TENANTS_PATH_PREFIX + "/{tenantId}/enrollments")
	public TenantEnrollmentResource enrollments(@PathParam("tenantId") String tenantId) {
		return tenantEnrollmentResource;
	}

	// ***********************
	// Tenant orders
	// ***********************

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
	private TenantJetCardOrderResource tenantJetCardOrderResource;

	@Path(TENANTS_PATH_PREFIX + "/{tenantId}/jetcard/orders")
	public TenantJetCardOrderResource tenantJetCardOrders(@PathParam("tenantId") String tenantId) {
		return tenantJetCardOrderResource;
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

	// ***********************
	// User orders
	// ***********************

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
	private JetcardOrderResource jetcardOrderResource;

	@Path(USERS_PATH_PREFIX + "/{userId}/jetcard/orders")
	public JetcardOrderResource jetcardOrders(@PathParam("userId") String userId) {
		return jetcardOrderResource;
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
