package net.aircommunity.platform.rest.user;

import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.json.JsonObject;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonView;

import io.micro.annotation.Authenticated;
import io.micro.annotation.RESTful;
import io.micro.common.Strings;
import io.swagger.annotations.Api;
import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.PaymentRequest;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.Address;
import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.model.domain.Passenger;
import net.aircommunity.platform.model.domain.Payment;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.rest.BaseResourceSupport;
import net.aircommunity.platform.service.order.CharterOrderService;
import net.aircommunity.platform.service.order.CommonOrderService;
import net.aircommunity.platform.service.security.AccountService;

/**
 * User resource.
 * 
 * @author Bin.Zhang
 */
@Api
@RESTful
@Path("user")
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_USER })
public class UserResource extends BaseResourceSupport {
	private static final Logger LOG = LoggerFactory.getLogger(UserResource.class);

	private static final String HEADER_FIRST_ORDER = "First-Order";

	@Resource
	private AccountService accountService;

	@Resource
	private CommonOrderService commonOrderService;

	@Resource
	private CharterOrderService charterOrderService;

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
	@JsonView(JsonViews.User.class)
	public Page<Order> listAllOrders(@QueryParam("status") String status,
			@QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize,
			@Context SecurityContext context) {
		String userId = context.getUserPrincipal().getName();
		LOG.debug("List user {} orders in status: {}, page: {}, pageSize: {}", userId, status, page, pageSize);
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
			return commonOrderService.listUserOrders(userId, page, pageSize);
		}
		return Page.emptyPage(page, pageSize);
	}

	/**
	 * Search (order within 6 months)
	 */
	@GET
	@Path("orders/search")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.User.class)
	public Response searchOrders(@QueryParam("orderNo") String orderNo,
			@Min(0) @DefaultValue("0") @QueryParam("days") int days, @QueryParam("page") @DefaultValue("0") int page,
			@QueryParam("pageSize") @DefaultValue("0") int pageSize, @Context SecurityContext context) {
		String userId = context.getUserPrincipal().getName();
		LOG.debug("Search user {} orders in orderNo: {}, days: {},  page: {}, pageSize: {}", userId, orderNo, days,
				page, pageSize);
		if (Strings.isNotBlank(orderNo)) {
			Order order = commonOrderService.findByOrderNo(orderNo);
			if (!order.isOwner(userId)) {
				throw new AirException(Codes.ORDER_ACCESS_DENIED, M.msg(M.ORDER_ACCESS_DENIED));
			}
			return Response.ok(order).build();
		}
		return Response.ok(commonOrderService.listUserOrders(userId, days, page, pageSize)).build();
	}

	/**
	 * First Order or not
	 */
	@HEAD
	@Path("orders/first")
	@Produces(MediaType.APPLICATION_JSON)
	public Response firstOrder(@Context SecurityContext context) {
		String userId = context.getUserPrincipal().getName();
		boolean exists = commonOrderService.existsOrderForUser(userId);
		return Response.noContent().header(HEADER_FIRST_ORDER, !exists).build();
	}

	/**
	 * Find
	 */
	@GET
	@Path("orders/{orderId}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.User.class)
	public Order findOrder(@PathParam("orderId") String orderId, @Context SecurityContext context) {
		String userId = context.getUserPrincipal().getName();
		Order order = commonOrderService.findOrder(orderId);
		if (!order.isOwner(userId)) {
			throw new AirException(Codes.ORDER_ACCESS_DENIED, M.msg(M.ORDER_ACCESS_DENIED));
		}
		return order;
	}

	/**
	 * Select a fleet (Only allow one vendor to be selected)
	 */
	@POST
	@Path("orders/{orderId}/fleet/select")
	public void selectFleet(@PathParam("orderId") String orderId,
			@NotNull @QueryParam("candidate") String fleetCandidateId) {
		charterOrderService.selectFleetCandidate(orderId, fleetCandidateId);
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
	public void refundOrder(@PathParam("orderId") String orderId, @NotNull JsonObject refundReason) {
		String reason = getRefundReason(refundReason);
		commonOrderService.requestOrderRefund(orderId, reason);
	}

	@POST
	@Path("orders/{orderId}/cancel")
	@Consumes(MediaType.APPLICATION_JSON)
	public void cancelOrder(@PathParam("orderId") String orderId, JsonObject request) {
		String reason = getReason(request);
		commonOrderService.cancelOrder(orderId, reason);
	}

	@DELETE
	@Path("orders/{orderId}")
	public void deleteOrder(@PathParam("orderId") String orderId, @Context SecurityContext context) {
		commonOrderService.softDeleteOrder(orderId);
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
	private UserCourseOrderResource userCourseOrderResource;

	@Path("course/orders")
	public UserCourseOrderResource courseOrders() {
		return userCourseOrderResource;
	}

}
