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
import net.aircommunity.platform.model.AirTourOrder;
import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.AirTourOrderService;
import net.aircommunity.rest.annotation.RESTful;

/**
 * AirTourOrder RESTful API.
 * 
 * @author Bin.Zhang
 */
@RESTful
@Path("tour-orders")
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_USER })
public class AirTourOrderResource {
	private static final Logger LOG = LoggerFactory.getLogger(AirTourOrderResource.class);

	@Resource
	private AirTourOrderService airTourOrderService;

	// ***********************
	// ADMIN/TENANT
	// ***********************

	/**
	 * List all order for Tenants
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT })
	public Response listAllForTenant(@PathParam("tenantId") String tenantId, @QueryParam("status") String status,
			@QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		Order.Status orderStatus = Order.Status.of(status);
		Page<AirTourOrder> result = Page.emptyPage(page, pageSize);
		if (orderStatus == null) {
			result = airTourOrderService.listTenantAirTourOrders(tenantId, page, pageSize);
		}
		else {
			result = airTourOrderService.listTenantAirTourOrders(tenantId, orderStatus, page, pageSize);
		}
		return Response.ok(result).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(result)).build();
	}

	// ***********************
	// ADMIN/USER
	// ***********************

	/**
	 * Create
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(@PathParam("userId") String userId, @NotNull @Valid AirTourOrder airTourOrder,
			@Context UriInfo uriInfo) {
		AirTourOrder created = airTourOrderService.createAirTourOrder(userId, airTourOrder);
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
		Page<AirTourOrder> result = airTourOrderService.listUserAirTourOrders(userId, page, pageSize);
		return Response.ok(result).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(result)).build();
	}

	/**
	 * Get
	 */
	@GET
	@Path("{airTourOrderId}")
	@Produces(MediaType.APPLICATION_JSON)
	public AirTourOrder find(@PathParam("airTourOrderId") String airTourOrderId) {
		return airTourOrderService.findAirTourOrder(airTourOrderId);
	}

	/**
	 * Update
	 */
	@PUT
	@Path("{airTourOrderId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public AirTourOrder update(@PathParam("airTourOrderId") String airTourOrderId,
			@NotNull @Valid AirTourOrder newAirTourOrder) {
		return airTourOrderService.updateAirTourOrder(airTourOrderId, newAirTourOrder);
	}

	/**
	 * Cancel order
	 */
	@POST
	@Path("{airTourOrderId}/cancel")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public AirTourOrder cancelOrder(@PathParam("airTourOrderId") String airTourOrderId) {
		return airTourOrderService.updateAirTourOrderStatus(airTourOrderId, Order.Status.CANCELLED);
	}

	/**
	 * Finish order
	 */
	@POST
	@Path("{airTourOrderId}/finish")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public AirTourOrder finishOrder(@PathParam("airTourOrderId") String airTourOrderId) {
		return airTourOrderService.updateAirTourOrderStatus(airTourOrderId, Order.Status.FINISHED);
	}

	/**
	 * Delete
	 */
	@DELETE
	@Path("{airTourOrderId}")
	public Response delete(@PathParam("airTourOrderId") String airTourOrderId) {
		airTourOrderService.deleteAirTourOrder(airTourOrderId);
		return Response.noContent().build();
	}

	/**
	 * Delete all
	 */
	@DELETE
	public Response deleteAll(@PathParam("userId") String userId) {
		airTourOrderService.deleteAirTourOrders(userId);
		return Response.noContent().build();
	}

}
