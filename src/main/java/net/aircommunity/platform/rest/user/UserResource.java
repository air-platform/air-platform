package net.aircommunity.platform.rest.user;

import javax.annotation.Resource;
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
import javax.ws.rs.core.SecurityContext;

import net.aircommunity.platform.common.net.HttpHeaders;
import net.aircommunity.platform.model.Address;
import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Passenger;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.service.AccountService;
import net.aircommunity.platform.service.CommonOrderService;
import net.aircommunity.rest.annotation.Authenticated;
import net.aircommunity.rest.annotation.RESTful;

/**
 * User resource.
 * 
 * @author Bin.Zhang
 */
@RESTful
@Path("user")
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_USER })
public class UserResource {

	@Resource
	private AccountService accountService;

	@Resource
	private CommonOrderService commonOrderService;

	// ***********************
	// User information
	// ***********************

	/**
	 * List all User addresses
	 */
	@GET
	@Path("addresses")
	@Produces(MediaType.APPLICATION_JSON)
	@Authenticated
	public Response listUserAddresses(@Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		return Response.ok(accountService.listUserAddresses(accountId)).build();
	}

	/**
	 * Add User address
	 */
	@POST
	@Path("addresses")
	@Consumes(MediaType.APPLICATION_JSON)
	@Authenticated
	public Response addUserAddress(@NotNull @Valid Address address, @Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		accountService.addUserAddress(accountId, address);
		return Response.noContent().build();
	}

	/**
	 * Delete User address
	 */
	@DELETE
	@Path("addresses/{addressId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Authenticated
	public Response removeUserAddress(@PathParam("addressId") String addressId, @Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		accountService.removeUserAddress(accountId, addressId);
		return Response.noContent().build();
	}

	/**
	 * List all User passengers
	 */
	@GET
	@Path("passengers")
	@Produces(MediaType.APPLICATION_JSON)
	@Authenticated
	public Response listUserPassengers(@Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		return Response.ok(accountService.listUserPassengers(accountId)).build();
	}

	/**
	 * Add User passenger
	 */
	@POST
	@Path("passengers")
	@Consumes(MediaType.APPLICATION_JSON)
	@Authenticated
	public Response addPassenger(@NotNull @Valid Passenger passenger, @Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		accountService.addUserPassenger(accountId, passenger);
		return Response.noContent().build();
	}

	/**
	 * Delete User passenger
	 */
	@DELETE
	@Path("passengers/{passengerId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Authenticated
	public Response removeUserPassenger(@PathParam("passengerId") String passengerId,
			@Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		accountService.removeUserPassenger(accountId, passengerId);
		return Response.noContent().build();
	}

	// *******//
	// Orders //
	// *******//

	/**
	 * List TODO more query?
	 */
	@GET
	@Path("orders")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listAllOrders(@QueryParam("page") @DefaultValue("1") int page,
			@QueryParam("pageSize") @DefaultValue("10") int pageSize, @Context SecurityContext context) {
		return buildOrdersResponse(null, page, pageSize, context);
	}

	@GET
	@Path("orders/pending")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listAllPendingOrders(@QueryParam("page") @DefaultValue("1") int page,
			@QueryParam("pageSize") @DefaultValue("10") int pageSize, @Context SecurityContext context) {
		return buildOrdersResponse(Order.Status.PENDING, page, pageSize, context);
	}

	@GET
	@Path("orders/paid")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listAllPaidOrders(@QueryParam("page") @DefaultValue("1") int page,
			@QueryParam("pageSize") @DefaultValue("10") int pageSize, @Context SecurityContext context) {
		return buildOrdersResponse(Order.Status.PAID, page, pageSize, context);
	}

	@GET
	@Path("orders/finished")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listAllFinishedOrders(@QueryParam("page") @DefaultValue("1") int page,
			@QueryParam("pageSize") @DefaultValue("10") int pageSize, @Context SecurityContext context) {
		return buildOrdersResponse(Order.Status.FINISHED, page, pageSize, context);
	}

	@GET
	@Path("orders/cancelled")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listAllCancelledOrders(@QueryParam("page") @DefaultValue("1") int page,
			@QueryParam("pageSize") @DefaultValue("10") int pageSize, @Context SecurityContext context) {
		return buildOrdersResponse(Order.Status.CANCELLED, page, pageSize, context);
	}

	private Response buildOrdersResponse(Order.Status status, int page, int pageSize, SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		Page<Order> result = commonOrderService.listAllUserOrders(accountId, status, page, pageSize);
		return Response.ok(result).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(result)).build();
	}

	// ***********************
	// Air Jet
	// ***********************
	@Resource
	private ChaterOrderResource chaterOrderResource;

	@Path("chater/orders")
	public ChaterOrderResource chaterOrders() {
		return chaterOrderResource;
	}

	@Resource
	private FerryFlightOrderResource ferryFlightOrderResource;

	@Path("ferryflight/orders")
	public FerryFlightOrderResource ferryFlightOrders() {
		return ferryFlightOrderResource;
	}

	@Resource
	private JetcardOrderResource jetcardOrderResource;

	@Path("jetcard/orders")
	public JetcardOrderResource jetcardOrders() {
		return jetcardOrderResource;
	}

	// ***********************
	// Air Transport
	// ***********************
	@Resource
	private AirTransportOrderResource airTransportOrderResource;

	@Path("airtransport/orders")
	public AirTransportOrderResource airTransportOrders() {
		return airTransportOrderResource;
	}

	// ***********************
	// Air Taxi
	// ***********************
	@Resource
	private AirTaxiOrderResource airTaxiOrderResource;

	@Path("airtaxi/orders")
	public AirTaxiOrderResource airTaxiOrders() {
		return airTaxiOrderResource;
	}

	// ***********************
	// Air Tour
	// ***********************
	@Resource
	private AirTourOrderResource airTourOrderResource;

	@Path("airtour/orders")
	public AirTourOrderResource airTourOrders() {
		return airTourOrderResource;
	}

	// ***********************
	// Course
	// ***********************
	@Resource
	private UserEnrollmentResource userEnrollmentResource;

	@Path("course/enrollments")
	public UserEnrollmentResource enrollments() {
		return userEnrollmentResource;
	}

}
