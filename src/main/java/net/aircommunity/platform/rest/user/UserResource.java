package net.aircommunity.platform.rest.user;

import java.util.List;

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
import javax.ws.rs.core.SecurityContext;

import io.micro.annotation.Authenticated;
import io.micro.annotation.RESTful;
import io.swagger.annotations.Api;
import net.aircommunity.platform.model.Address;
import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Passenger;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.service.AccountService;
import net.aircommunity.platform.service.CommonOrderService;

/**
 * User resource.
 * 
 * @author Bin.Zhang
 */
@Api
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
	@Authenticated
	@Produces(MediaType.APPLICATION_JSON)
	public List<Address> listUserAddresses(@Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		return accountService.listUserAddresses(accountId);
	}

	/**
	 * Add User address
	 */
	@POST
	@Path("addresses")
	@Authenticated
	@Consumes(MediaType.APPLICATION_JSON)
	public void addUserAddress(@NotNull @Valid Address address, @Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		accountService.addUserAddress(accountId, address);
	}

	/**
	 * Delete User address
	 */
	@DELETE
	@Path("addresses/{addressId}")
	@Authenticated
	@Produces(MediaType.APPLICATION_JSON)
	public void removeUserAddress(@PathParam("addressId") String addressId, @Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		accountService.removeUserAddress(accountId, addressId);
	}

	/**
	 * List all User passengers
	 */
	@GET
	@Path("passengers")
	@Authenticated
	@Produces(MediaType.APPLICATION_JSON)
	public List<Passenger> listUserPassengers(@Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		return accountService.listUserPassengers(accountId);
	}

	/**
	 * Add User passenger
	 */
	@POST
	@Path("passengers")
	@Authenticated
	@Consumes(MediaType.APPLICATION_JSON)
	public Passenger addPassenger(@NotNull @Valid Passenger passenger, @Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		return accountService.addUserPassenger(accountId, passenger);
	}

	/**
	 * Delete User passenger
	 */
	@DELETE
	@Path("passengers/{passengerId}")
	@Authenticated
	@Produces(MediaType.APPLICATION_JSON)
	public void removeUserPassenger(@PathParam("passengerId") String passengerId, @Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		accountService.removeUserPassenger(accountId, passengerId);
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
	public Page<Order> listAllOrders(@QueryParam("page") @DefaultValue("1") int page,
			@QueryParam("pageSize") @DefaultValue("10") int pageSize, @Context SecurityContext context) {
		return buildOrdersResponse(null, page, pageSize, context);
	}

	@GET
	@Path("orders/pending")
	@Produces(MediaType.APPLICATION_JSON)
	public Page<Order> listAllPendingOrders(@QueryParam("page") @DefaultValue("1") int page,
			@QueryParam("pageSize") @DefaultValue("10") int pageSize, @Context SecurityContext context) {
		return buildOrdersResponse(Order.Status.PENDING, page, pageSize, context);
	}

	@GET
	@Path("orders/paid")
	@Produces(MediaType.APPLICATION_JSON)
	public Page<Order> listAllPaidOrders(@QueryParam("page") @DefaultValue("1") int page,
			@QueryParam("pageSize") @DefaultValue("10") int pageSize, @Context SecurityContext context) {
		return buildOrdersResponse(Order.Status.PAID, page, pageSize, context);
	}

	@GET
	@Path("orders/finished")
	@Produces(MediaType.APPLICATION_JSON)
	public Page<Order> listAllFinishedOrders(@QueryParam("page") @DefaultValue("1") int page,
			@QueryParam("pageSize") @DefaultValue("10") int pageSize, @Context SecurityContext context) {
		return buildOrdersResponse(Order.Status.FINISHED, page, pageSize, context);
	}

	@GET
	@Path("orders/cancelled")
	@Produces(MediaType.APPLICATION_JSON)
	public Page<Order> listAllCancelledOrders(@QueryParam("page") @DefaultValue("1") int page,
			@QueryParam("pageSize") @DefaultValue("10") int pageSize, @Context SecurityContext context) {
		return buildOrdersResponse(Order.Status.CANCELLED, page, pageSize, context);
	}

	private Page<Order> buildOrdersResponse(Order.Status status, int page, int pageSize, SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		return commonOrderService.listUserOrders(accountId, status, page, pageSize);
	}

	@GET
	@Path("orders/{orderId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Order findOrder(@PathParam("orderId") String orderId, @Context SecurityContext context) {
		return commonOrderService.findOrder(orderId);
	}

	@POST
	@Path("orders/{orderId}/pay")
	@Produces(MediaType.APPLICATION_JSON)
	public void payOrder(@PathParam("orderId") String orderId, @Context SecurityContext context) {
		commonOrderService.updateOrderStatus(orderId, Order.Status.PAID);
	}

	@POST
	@Path("orders/{orderId}/cancel")
	@Produces(MediaType.APPLICATION_JSON)
	public void cancelOrder(@PathParam("orderId") String orderId, @Context SecurityContext context) {
		commonOrderService.updateOrderStatus(orderId, Order.Status.CANCELLED);
	}

	@DELETE
	@Path("orders/{orderId}")
	@Produces(MediaType.APPLICATION_JSON)
	public void deleteOrder(@PathParam("orderId") String orderId, @Context SecurityContext context) {
		commonOrderService.updateOrderStatus(orderId, Order.Status.DELETED);
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
