package net.aircommunity.platform.rest.user;

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

import io.micro.annotation.RESTful;
import net.aircommunity.platform.common.net.HttpHeaders;
import net.aircommunity.platform.model.AirTourOrder;
import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.AirTourOrderService;

/**
 * AirTourOrder RESTful API.
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_USER })
public class AirTourOrderResource {
	private static final Logger LOG = LoggerFactory.getLogger(AirTourOrderResource.class);

	@Resource
	private AirTourOrderService airTourOrderService;

	/**
	 * Create
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(@PathParam("userId") String userId, @NotNull @Valid AirTourOrder order,
			@Context UriInfo uriInfo) {
		AirTourOrder created = airTourOrderService.createAirTourOrder(userId, order);
		URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
		LOG.debug("Created {}", uri);
		return Response.created(uri).build();
	}

	/**
	 * Find
	 */
	@GET
	@Path("{orderId}")
	@Produces(MediaType.APPLICATION_JSON)
	public AirTourOrder find(@PathParam("orderId") String orderId) {
		return airTourOrderService.findAirTourOrder(orderId);
	}

	/**
	 * List TODO query
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response list(@PathParam("userId") String userId, @QueryParam("status") String status,
			@QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		Page<AirTourOrder> result = airTourOrderService.listUserAirTourOrders(userId, Order.Status.of(status), page,
				pageSize);
		return Response.ok(result).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(result)).build();
	}

	/**
	 * Update
	 */
	@PUT
	@Path("{orderId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public AirTourOrder update(@PathParam("orderId") String orderId, @NotNull @Valid AirTourOrder newOrder) {
		return airTourOrderService.updateAirTourOrder(orderId, newOrder);
	}

	/**
	 * Cancel order
	 */
	@POST
	@Path("{orderId}/cancel")
	public Response cancelOrder(@PathParam("orderId") String orderId) {
		airTourOrderService.updateAirTourOrderStatus(orderId, Order.Status.CANCELLED);
		return Response.noContent().build();
	}

	/**
	 * Finish order (TODO who finish it?)
	 */
	@POST
	@Path("{orderId}/finish")
	public Response finishOrder(@PathParam("orderId") String orderId) {
		airTourOrderService.updateAirTourOrderStatus(orderId, Order.Status.FINISHED);
		return Response.noContent().build();
	}

	/**
	 * Delete (mark order as DELETED)
	 */
	@DELETE
	@Path("{orderId}")
	public Response delete(@PathParam("orderId") String orderId) {
		airTourOrderService.updateAirTourOrderStatus(orderId, Order.Status.DELETED);
		return Response.noContent().build();
	}

	// **************
	// ADMIN ONLY
	// **************

	/**
	 * Mark order as Paid
	 */
	@POST
	@Path("{orderId}/paid")
	@RolesAllowed(Roles.ROLE_ADMIN)
	public Response markPaidOrder(@PathParam("orderId") String orderId) {
		airTourOrderService.updateAirTourOrderStatus(orderId, Order.Status.PAID);
		return Response.noContent().build();
	}

	/**
	 * Delete
	 */
	@DELETE
	@Path("{orderId}")
	@RolesAllowed(Roles.ROLE_ADMIN)
	public Response forceDelete(@PathParam("orderId") String orderId) {
		airTourOrderService.deleteAirTourOrder(orderId);
		return Response.noContent().build();
	}

	/**
	 * Delete all
	 */
	@DELETE
	@RolesAllowed(Roles.ROLE_ADMIN)
	public Response deleteAll(@PathParam("userId") String userId) {
		airTourOrderService.deleteAirTourOrders(userId);
		return Response.noContent().build();
	}

}
