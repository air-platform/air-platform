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
import net.aircommunity.platform.model.AirTransportOrder;
import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.AirTransportOrderService;
import net.aircommunity.rest.annotation.RESTful;

/**
 * AirTransportOrder RESTful API.
 * 
 * @author Bin.Zhang
 */
@RESTful
@Path("transport-orders")
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_USER })
public class AirTransportOrderResource {
	private static final Logger LOG = LoggerFactory.getLogger(AirTransportOrderResource.class);

	@Resource
	private AirTransportOrderService airTransportOrderService;

	/**
	 * List all for Tenants
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listAllForTenant(@PathParam("tenantId") String tenantId, @QueryParam("status") String status,
			@QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		Order.Status orderStatus = Order.Status.of(status);
		Page<AirTransportOrder> result = Page.emptyPage(page, pageSize);
		if (orderStatus != null) {
			result = airTransportOrderService.listTenantAirTransportOrders(tenantId, page, pageSize);
		}
		else {
			result = airTransportOrderService.listTenantAirTransportOrders(tenantId, orderStatus, page, pageSize);
		}
		return Response.ok(result).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(result)).build();
	}

	//

	/**
	 * Create
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(@PathParam("userId") String userId, @NotNull @Valid AirTransportOrder airTransportOrder,
			@Context UriInfo uriInfo) {
		AirTransportOrder created = airTransportOrderService.createAirTransportOrder(userId, airTransportOrder);
		URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
		LOG.debug("Created {}", uri);
		return Response.created(uri).build();
	}

	/**
	 * List all
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listAll(@PathParam("userId") String userId, @QueryParam("status") String status,
			@QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		Page<AirTransportOrder> result = airTransportOrderService.listUserAirTransportOrders(userId, page, pageSize);
		return Response.ok(result).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(result)).build();
	}

	/**
	 * Get
	 */
	@GET
	@Path("{airTransportOrderId}")
	@Produces(MediaType.APPLICATION_JSON)
	public AirTransportOrder find(@PathParam("airTransportOrderId") String airTransportOrderId) {
		return airTransportOrderService.findAirTransportOrder(airTransportOrderId);
	}

	/**
	 * Update
	 */
	@PUT
	@Path("{airTransportOrderId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public AirTransportOrder update(@PathParam("airTransportOrderId") String airTransportOrderId,
			@NotNull @Valid AirTransportOrder newAirTransportOrder) {
		return airTransportOrderService.updateAirTransportOrder(airTransportOrderId, newAirTransportOrder);
	}

	/**
	 * Cancel order
	 */
	@POST
	@Path("{airTransportOrderId}/cancel")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public AirTransportOrder cancelOrder(@PathParam("airTransportOrderId") String airTransportOrderId) {
		return airTransportOrderService.updateAirTransportOrderStatus(airTransportOrderId, Order.Status.CANCELLED);
	}

	/**
	 * Finish order
	 */
	@POST
	@Path("{airTransportOrderId}/finish")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public AirTransportOrder finishOrder(@PathParam("airTransportOrderId") String airTransportOrderId) {
		return airTransportOrderService.updateAirTransportOrderStatus(airTransportOrderId, Order.Status.FINISHED);
	}

	/**
	 * Delete
	 */
	@DELETE
	@Path("{airTransportOrderId}")
	public Response delete(@PathParam("airTransportOrderId") String airTransportOrderId) {
		airTransportOrderService.deleteAirTransportOrder(airTransportOrderId);
		return Response.noContent().build();
	}

	/**
	 * Delete all
	 */
	@DELETE
	public Response deleteAll(@PathParam("userId") String userId) {
		airTransportOrderService.deleteAirTransportOrders(userId);
		return Response.noContent().build();
	}

}
