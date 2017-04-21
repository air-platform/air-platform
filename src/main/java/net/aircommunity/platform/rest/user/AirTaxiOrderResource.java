package net.aircommunity.platform.rest.user;

import java.net.URI;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
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
import net.aircommunity.platform.model.AirTaxiOrder;
import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.rest.BaseOrderResource;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.AirTaxiOrderService;

/**
 * AirTaxiOrder RESTful API.
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_USER })
public class AirTaxiOrderResource extends BaseOrderResource<AirTaxiOrder> {
	private static final Logger LOG = LoggerFactory.getLogger(AirTaxiOrderResource.class);

	@Resource
	private AirTaxiOrderService airTaxiOrderService;

	/**
	 * Create
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(@PathParam("userId") String userId, @NotNull @Valid AirTaxiOrder order,
			@Context UriInfo uriInfo) {
		AirTaxiOrder created = airTaxiOrderService.createAirTaxiOrder(userId, order);
		URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
		LOG.debug("Created {}", uri);
		return Response.created(uri).build();
	}

	/**
	 * List TODO query
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response list(@PathParam("userId") String userId, @QueryParam("status") String status,
			@QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		Page<AirTaxiOrder> result = airTaxiOrderService.listUserAirTaxiOrders(userId, Order.Status.of(status), page,
				pageSize);
		return buildPageResponse(result);
	}

	/**
	 * Update
	 */
	@PUT
	@Path("{orderId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public AirTaxiOrder update(@PathParam("orderId") String orderId, @NotNull @Valid AirTaxiOrder newOrder) {
		return airTaxiOrderService.updateAirTaxiOrder(orderId, newOrder);
	}

	// **************
	// ADMIN ONLY
	// **************

	/**
	 * Delete
	 */
	// @DELETE
	// @Path("{orderId}")
	// @RolesAllowed(Roles.ROLE_ADMIN)
	// public Response forceDelete(@PathParam("orderId") String orderId) {
	// airTaxiOrderService.deleteAirTaxiOrder(orderId);
	// return Response.noContent().build();
	// }
	//
	// /**
	// * Delete all
	// */
	// @DELETE
	// @RolesAllowed(Roles.ROLE_ADMIN)
	// public Response deleteAll(@PathParam("userId") String userId) {
	// airTaxiOrderService.deleteAirTaxiOrders(userId);
	// return Response.noContent().build();
	// }
	// /**
	// * Find
	// */
	// @GET
	// @Path("{orderId}")
	// @Produces(MediaType.APPLICATION_JSON)
	// public AirTaxiOrder find(@PathParam("orderId") String orderId) {
	// return airTaxiOrderService.findAirTaxiOrder(orderId);
	// }
	// /**
	// * Cancel order
	// */
	// @POST
	// @Path("{orderId}/cancel")
	// public Response cancelOrder(@PathParam("orderId") String orderId) {
	// airTaxiOrderService.updateAirTaxiOrderStatus(orderId, Order.Status.CANCELLED);
	// return Response.noContent().build();
	// }
	// /**
	// * Delete (mark order as DELETED)
	// */
	// @DELETE
	// @Path("{orderId}")
	// public Response delete(@PathParam("orderId") String orderId) {
	// airTaxiOrderService.updateAirTaxiOrderStatus(orderId, Order.Status.DELETED);
	// return Response.noContent().build();
	// }
	// /**
	// * Mark order as Paid
	// */
	// @POST
	// @Path("{orderId}/paid")
	// @RolesAllowed(Roles.ROLE_ADMIN)
	// public Response markPaidOrder(@PathParam("orderId") String orderId) {
	// airTaxiOrderService.updateAirTaxiOrderStatus(orderId, Order.Status.PAID);
	// return Response.noContent().build();
	// }
}
