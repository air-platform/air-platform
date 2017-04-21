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
import net.aircommunity.platform.model.CharterOrder;
import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.rest.BaseOrderResource;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.CharterOrderService;

/**
 * CharterOrder RESTful API. NOTE: <b>all permission</b> for ADMIN/TENANT
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_USER })
public class ChaterOrderResource extends BaseOrderResource<CharterOrder> {
	private static final Logger LOG = LoggerFactory.getLogger(ChaterOrderResource.class);

	@Resource
	private CharterOrderService charterOrderService;

	/**
	 * Create
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(@PathParam("userId") String userId, @NotNull @Valid CharterOrder order,
			@Context UriInfo uriInfo) {
		CharterOrder created = charterOrderService.createCharterOrder(userId, order);
		URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
		LOG.debug("Created {}", uri);
		return Response.created(uri).build();
	}

	/**
	 * List
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response list(@PathParam("userId") String userId, @QueryParam("status") String status,
			@QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		Page<CharterOrder> result = charterOrderService.listUserCharterOrders(userId, Order.Status.of(status), page,
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
	public CharterOrder update(@PathParam("orderId") String orderId, @NotNull @Valid CharterOrder newOrder) {
		return charterOrderService.updateCharterOrder(orderId, newOrder);
	}

	/**
	 * Select a fleet (Only allow one vendor to be selected)
	 */
	@POST
	@Path("{orderId}/select")
	public Response selectFleet(@PathParam("orderId") String orderId,
			@NotNull @QueryParam("candidate") String fleetCandidateId) {
		charterOrderService.selectFleetCandidate(orderId, fleetCandidateId);
		return Response.noContent().build();
	}

	// /**
	// * Find
	// */
	// @GET
	// @Path("{orderId}")
	// @Produces(MediaType.APPLICATION_JSON)
	// public CharterOrder find(@PathParam("orderId") String orderId) {
	// return charterOrderService.findCharterOrder(orderId);
	// }
	//
	// /**
	// * Cancel order
	// */
	// @POST
	// @Path("{orderId}/cancel")
	// public Response cancelOrder(@PathParam("orderId") String orderId) {
	// charterOrderService.updateCharterOrderStatus(orderId, Order.Status.CANCELLED);
	// return Response.noContent().build();
	// }
	//
	// /**
	// * Delete (mark order as DELETED)
	// */
	// @DELETE
	// @Path("{orderId}")
	// public Response delete(@PathParam("orderId") String orderId) {
	// charterOrderService.updateCharterOrderStatus(orderId, Order.Status.DELETED);
	// return Response.noContent().build();
	// }
	//
	// // **************
	// // ADMIN ONLY
	// // **************
	//
	// /**
	// * Mark order as Paid
	// */
	// @POST
	// @Path("{orderId}/paid")
	// @RolesAllowed(Roles.ROLE_ADMIN)
	// public Response markPaidOrder(@PathParam("orderId") String orderId) {
	// charterOrderService.updateCharterOrderStatus(orderId, Order.Status.PAID);
	// return Response.noContent().build();
	// }
	//
	// /**
	// * Delete
	// */
	// @DELETE
	// @Path("{orderId}/force")
	// @RolesAllowed(Roles.ROLE_ADMIN)
	// public Response forceDelete(@PathParam("orderId") String orderId) {
	// charterOrderService.deleteCharterOrder(orderId);
	// return Response.noContent().build();
	// }
	//
	// /**
	// * Delete all
	// */
	// @DELETE
	// @RolesAllowed(Roles.ROLE_ADMIN)
	// public Response deleteAll(@PathParam("userId") String userId) {
	// charterOrderService.deleteCharterOrders(userId);
	// return Response.noContent().build();
	// }

}
