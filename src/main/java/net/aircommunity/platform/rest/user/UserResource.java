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
import io.micro.common.Strings;
import io.swagger.annotations.Api;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.PaymentRequest;
import net.aircommunity.platform.model.RefundRequest;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.Address;
import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.model.domain.Passenger;
import net.aircommunity.platform.model.domain.Payment;
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
	@Authenticated
	@Path("passengers")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Passenger addPassenger(@NotNull @Valid Passenger passenger, @Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		return accountService.addUserPassenger(accountId, passenger);
	}

	/**
	 * Delete User passenger
	 */
	@DELETE
	@Authenticated
	@Path("passengers/{passengerId}")
	public void removeUserPassenger(@PathParam("passengerId") String passengerId, @Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		accountService.removeUserPassenger(accountId, passengerId);
	}

	// *******//
	// Orders //
	// *******//

	private static final String ORDER_FILTER_STATUS_PENDING = "pending";
	private static final String ORDER_FILTER_STATUS_FINISHED = "finished";
	private static final String ORDER_FILTER_STATUS_CANCELLED = "cancelled";
	private static final String ORDER_FILTER_STATUS_REFUND = "refund";

	/**
	 * List All
	 */
	@GET
	@Path("orders")
	@Produces(MediaType.APPLICATION_JSON)
	public Page<Order> listAllOrders(@QueryParam("status") String status,
			@QueryParam("page") @DefaultValue("1") int page, @QueryParam("pageSize") @DefaultValue("10") int pageSize,
			@Context SecurityContext context) {
		String userId = context.getUserPrincipal().getName();
		switch (status) {
		case ORDER_FILTER_STATUS_PENDING:
			// PUBLISHED + CREATED + PAID + CONFIRMED + CONTRACT_SIGNED + TICKET_RELEASED + REFUND_REQUESTED + REFUNDING
			// etc.
			return commonOrderService.listUserPendingOrders(userId, page, pageSize);

		case ORDER_FILTER_STATUS_FINISHED:
			// REFUNDED FINISHED CLOSED
			return commonOrderService.listUserFinishedOrders(userId, page, pageSize);

		case ORDER_FILTER_STATUS_REFUND:
			// REFUND_REQUESTED, REFUNDING
			return commonOrderService.listUserRefundOrders(userId, page, pageSize);

		case ORDER_FILTER_STATUS_CANCELLED:
			// CANCELLED
			return commonOrderService.listUserOrders(userId, Order.Status.CANCELLED, page, pageSize);

		default: // noops
		}
		if (Strings.isBlank(status)) {
			return commonOrderService.listUserOrders(userId, null, page, pageSize);
		}
		return Page.emptyPage(page, pageSize);
	}

	/**
	 * Find
	 */
	@GET
	@Path("orders/{orderId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Order findOrder(@PathParam("orderId") String orderId, @Context SecurityContext context) {
		return commonOrderService.findOrder(orderId);
	}

	// ***********************************
	// Order Actions
	// - pay (via Alipay etc.)
	// - refund
	// - cancel
	// ***********************************

	@POST
	@Path("orders/{orderId}/pay/{paymentMethod}")
	@Produces(MediaType.APPLICATION_JSON)
	public PaymentRequest payOrder(@PathParam("orderId") String orderId,
			@PathParam("paymentMethod") Payment.Method paymentMethod) {
		return commonOrderService.requestOrderPayment(orderId, paymentMethod);
	}

	@POST
	@Path("orders/{orderId}/refund")
	@Consumes(MediaType.APPLICATION_JSON)
	public void refundOrder(@PathParam("orderId") String orderId, @NotNull @Valid RefundRequest request) {
		commonOrderService.requestOrderRefund(orderId, request);
	}

	@POST
	@Path("orders/{orderId}/cancel")
	public void cancelOrder(@PathParam("orderId") String orderId) {
		commonOrderService.cancelOrder(orderId);
	}

	@DELETE
	@Path("orders/{orderId}")
	public void deleteOrder(@PathParam("orderId") String orderId, @Context SecurityContext context) {
		commonOrderService.updateOrderStatus(orderId, Order.Status.DELETED);
	}

	// ****************************
	// XXX NOTE: NOT USED FOR NOW
	// Detailed Order APIs
	// ****************************

	// ***********************
	// Air Jet
	// ***********************
	@Resource
	private CharterOrderResource charterOrderResource;

	// @Path("chater/orders")
	@Path("charter/orders")
	public CharterOrderResource charterOrders() {
		return charterOrderResource;
	}

	@Resource
	private FerryFlightOrderResource ferryFlightOrderResource;

	@Path("ferryflight/orders")
	public FerryFlightOrderResource ferryFlightOrders() {
		return ferryFlightOrderResource;
	}

	@Resource
	private JetTravelOrderResource jetTravelOrderResource;

	@Path("jettravel/orders")
	public JetTravelOrderResource jetTravelOrders() {
		return jetTravelOrderResource;
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
	private UserCourseOrderResource userEnrollmentResource;

	@Path("course/orders")
	public UserCourseOrderResource enrollments() {
		return userEnrollmentResource;
	}

}
