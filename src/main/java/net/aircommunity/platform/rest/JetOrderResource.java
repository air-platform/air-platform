package net.aircommunity.platform.rest;

import java.net.URI;

import javax.annotation.Resource;
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

import net.aircommunity.platform.common.net.HttpHeaders;
import net.aircommunity.platform.model.CharterOrder;
import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.CharterOrderService;
import net.aircommunity.platform.service.FerryFlightOrderService;
import net.aircommunity.platform.service.JetCardOrderService;
import net.aircommunity.rest.annotation.RESTful;

/**
 * AirJet Orders RESTful API.
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT, Roles.ROLE_USER })
public class JetOrderResource {
	private static final Logger LOG = LoggerFactory.getLogger(JetOrderResource.class);

	@Resource
	private CharterOrderService charterOrderService;

	@Resource
	private FerryFlightOrderService ferryFlightOrderService;

	@Resource
	private JetCardOrderService jetCardOrderService;

	// *************
	// CharterOrder
	// *************

	/**
	 * Create
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(@PathParam("userId") String userId, @NotNull @Valid CharterOrder charterOrder,
			@Context UriInfo uriInfo) {
		CharterOrder created = charterOrderService.createCharterOrder(userId, charterOrder);
		URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
		LOG.debug("Created {}", uri);
		return Response.created(uri).build();
	}

	/**
	 * List all
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listAll(@PathParam("userId") String userId, @QueryParam("page") @DefaultValue("0") int page,
			@QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		Page<CharterOrder> result = charterOrderService.listUserCharterOrders(userId, page, pageSize);
		return Response.ok(result).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(result)).build();
	}

	/**
	 * Get
	 */
	@GET
	@Path("{charterOrderId}")
	@Produces(MediaType.APPLICATION_JSON)
	public CharterOrder find(@PathParam("charterOrderId") String charterOrderId) {
		return charterOrderService.findCharterOrder(charterOrderId);
	}

	@GET
	@Path("number/{orderNo}")
	@Produces(MediaType.APPLICATION_JSON)
	public CharterOrder findByOrderNo(@PathParam("orderNo") String orderNo) {
		return charterOrderService.findCharterOrderByOrderNo(orderNo);
	}

	/**
	 * Update
	 */
	@PUT
	@Path("{charterOrderId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public CharterOrder update(@PathParam("charterOrderId") String charterOrderId,
			@NotNull @Valid CharterOrder newCharterOrder) {
		return charterOrderService.updateCharterOrder(charterOrderId, newCharterOrder);
	}

	/**
	 * Select a fleet
	 */
	@POST
	@Path("{charterOrderId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public CharterOrder selectFleet(@PathParam("charterOrderId") String charterOrderId,
			@QueryParam("fleetCandidateId") String fleetCandidateId) {
		return charterOrderService.selectFleetCandidate(charterOrderId, fleetCandidateId);
	}

	/**
	 * Cancel order
	 */
	@POST
	@Path("{charterOrderId}/cancel")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public CharterOrder cancelOrder(@PathParam("charterOrderId") String charterOrderId) {
		return charterOrderService.updateCharterOrderStatus(charterOrderId, Order.Status.CANCELLED);
	}

	/**
	 * Finish order
	 */
	@POST
	@Path("{charterOrderId}/finish")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public CharterOrder finishOrder(@PathParam("charterOrderId") String charterOrderId) {
		return charterOrderService.updateCharterOrderStatus(charterOrderId, Order.Status.FINISHED);
	}

	/**
	 * Delete
	 */
	@DELETE
	@Path("{charterOrderId}")
	public Response delete(@PathParam("charterOrderId") String charterOrderId) {
		charterOrderService.deleteCharterOrder(charterOrderId);
		return Response.noContent().build();
	}

	/**
	 * Delete all
	 */
	@DELETE
	public Response deleteAll(@PathParam("userId") String userId) {
		charterOrderService.deleteCharterOrders(userId);
		return Response.noContent().build();
	}

	// *************
	// JetCard
	// *************

	// Tenant
	/**
	 * List all
	 */
	@GET
	@Path("tenant")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listAllForTenant(@PathParam("tenantId") String tenantId,
			@QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		Page<CharterOrder> result = charterOrderService.listTenantCharterOrders(tenantId, page, pageSize);
		return Response.ok(result).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(result)).build();
	}

}
