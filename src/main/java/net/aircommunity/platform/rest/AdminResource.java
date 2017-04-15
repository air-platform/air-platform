package net.aircommunity.platform.rest;

import java.net.URI;

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

import net.aircommunity.platform.common.net.HttpHeaders;
import net.aircommunity.platform.model.Account;
import net.aircommunity.platform.model.AccountRequest;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Role;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.service.AccountService;
import net.aircommunity.rest.annotation.RESTful;
import net.aircommunity.rest.core.security.AccessTokenService;

/**
 * Admin RESTful API.
 * 
 * @author Bin.Zhang
 */
@RESTful
@Path("platform")
@RolesAllowed(Roles.ROLE_ADMIN)
public class AdminResource {
	private static final Logger LOG = LoggerFactory.getLogger(AdminResource.class);

	private static final String TENANTS_PATH_PREFIX = "tenants";

	@Resource
	private AccountService accountService;

	@Resource
	private AccessTokenService accessTokenService;

	@Resource
	private FleetResource fleetResource;

	@Resource
	private FerryFlightResource ferryFlightResource;

	@Resource
	private JetCardResource jetCardResource;

	@Resource
	private JetOrderResource jetOrderResource;

	/**
	 * Ping server to make sure server is still alive, it is used for monitoring purpose.
	 */
	@GET
	@Path("ping")
	@PermitAll
	public Response ping() {
		return Response.noContent().build();
	}

	// **************************************
	// Account administration
	// **************************************

	/**
	 * Create a tenant account
	 */
	@POST
	@Path(TENANTS_PATH_PREFIX)
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
	@Path(TENANTS_PATH_PREFIX)
	@Produces(MediaType.APPLICATION_JSON)
	public Response listAllAccounts(@QueryParam("role") String role, @QueryParam("page") @DefaultValue("0") int page,
			@QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		Page<Account> accountPage = accountService.listAccounts(Role.of(role), page, pageSize);
		return Response.ok(accountPage).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(accountPage))
				.build();
	}

	/**
	 * Gets a account
	 */
	@GET
	@Path(TENANTS_PATH_PREFIX + "/{accountId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Account findAccount(@PathParam("accountId") String accountId) {
		return accountService.findAccount(accountId);
	}

	/**
	 * Reset password for a account
	 */
	@POST
	@Path(TENANTS_PATH_PREFIX + "/{accountId}/password/reset")
	public Response resetAccountPassword(@PathParam("accountId") String accountId) {
		accountService.resetPassword(accountId);
		return Response.noContent().build();
	}

	/**
	 * Lock a account
	 */
	@POST
	@Path(TENANTS_PATH_PREFIX + "/{accountId}/lock")
	public Response lockAccount(@PathParam("accountId") String accountId) {
		accountService.updateAccountStatus(accountId, Account.Status.DISABLED);
		return Response.noContent().build();
	}

	/**
	 * Unlock a account
	 */
	@POST
	@Path(TENANTS_PATH_PREFIX + "/{accountId}/unlock")
	public Response unlockAccount(@PathParam("accountId") String accountId) {
		accountService.updateAccountStatus(accountId, Account.Status.ENABLED);
		return Response.noContent().build();
	}

	/**
	 * Delete a account
	 */
	@Path(TENANTS_PATH_PREFIX + "/{accountId}")
	@DELETE
	public Response deleteAccount(@PathParam("accountId") String accountId) {
		accountService.deleteAccount(accountId);
		return Response.noContent().build();
	}

	// *************
	// Tenant
	// *************

	// Air jet
	@Path(TENANTS_PATH_PREFIX + "/{tenantId}/fleets")
	public FleetResource fleets(@PathParam("tenantId") String tenantId) {
		return fleetResource;
	}

	@Path(TENANTS_PATH_PREFIX + "/{tenantId}/ferryflights")
	public FerryFlightResource ferryflights(@PathParam("tenantId") String tenantId) {
		return ferryFlightResource;
	}

	@Path(TENANTS_PATH_PREFIX + "/{tenantId}/jetcards")
	public JetCardResource jetcards(@PathParam("tenantId") String tenantId) {
		return jetCardResource;
	}

	// aircraft
	@Resource
	private AircraftResource aircraftResource;

	@Path(TENANTS_PATH_PREFIX + "/{tenantId}/aircrafts")
	public AircraftResource aircrafts(@PathParam("tenantId") String tenantId) {
		return aircraftResource;
	}

	// transports
	@Resource
	private AirTransportResource airTransportResource;

	@Path(TENANTS_PATH_PREFIX + "/{tenantId}/transports")
	public AirTransportResource transports(@PathParam("tenantId") String tenantId) {
		return airTransportResource;
	}

	// taxis
	@Resource
	private AirTaxiResource airTaxiResource;

	@Path(TENANTS_PATH_PREFIX + "/{tenantId}/taxis")
	public AirTaxiResource taxis(@PathParam("tenantId") String tenantId) {
		return airTaxiResource;
	}

	// tour
	@Resource
	private AirTourResourse airTourResourse;

	@Path(TENANTS_PATH_PREFIX + "/{tenantId}/tours")
	public AirTourResourse tours(@PathParam("tenantId") String tenantId) {
		return airTourResourse;
	}

	// school
	@Resource
	private SchoolResource schoolResource;

	@Path(TENANTS_PATH_PREFIX + "/{tenantId}/schools")
	public SchoolResource schools(@PathParam("tenantId") String tenantId) {
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

	// ***********************
	// orders
	// ***********************
	@Resource
	private AirTransportOrderResource airTransportOrderResource;

	@Path(TENANTS_PATH_PREFIX + "/{tenantId}/transport-orders")
	public AirTransportOrderResource airTransportOrders(@PathParam("tenantId") String tenantId) {
		return airTransportOrderResource;
	}

	// jet orders
	@Path(TENANTS_PATH_PREFIX + "/{tenantId}/jetorders")
	public JetOrderResource jetorders(@PathParam("tenantId") String tenantId) {
		return jetOrderResource;
	}

}
