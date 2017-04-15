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
import net.aircommunity.platform.model.AirTaxiOrder;
import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.AirTaxiOrderService;
import net.aircommunity.rest.annotation.RESTful;

/**
 * AirTaxiOrder RESTful API.
 * 
 * @author Bin.Zhang
 */
@RESTful
@Path("taxi-orders")
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_USER })
public class AirTaxiOrderResource {
	private static final Logger LOG = LoggerFactory.getLogger(AirTaxiOrderResource.class);

	@Resource
	private AirTaxiOrderService airTaxiOrderService;

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
		Page<AirTaxiOrder> result = Page.emptyPage(page, pageSize);
		if (orderStatus == null) {
			result = airTaxiOrderService.listTenantAirTaxiOrders(tenantId, page, pageSize);
		}
		else {
			result = airTaxiOrderService.listTenantAirTaxiOrders(tenantId, orderStatus, page, pageSize);
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
	public Response create(@PathParam("userId") String userId, @NotNull @Valid AirTaxiOrder AirTaxiOrder,
			@Context UriInfo uriInfo) {
		AirTaxiOrder created = airTaxiOrderService.createAirTaxiOrder(userId, AirTaxiOrder);
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
		Page<AirTaxiOrder> result = airTaxiOrderService.listUserAirTaxiOrders(userId, page, pageSize);
		return Response.ok(result).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(result)).build();
	}

	/**
	 * Get
	 */
	@GET
	@Path("{AirTaxiOrderId}")
	@Produces(MediaType.APPLICATION_JSON)
	public AirTaxiOrder find(@PathParam("AirTaxiOrderId") String AirTaxiOrderId) {
		return airTaxiOrderService.findAirTaxiOrder(AirTaxiOrderId);
	}

	/**
	 * Update
	 */
	@PUT
	@Path("{AirTaxiOrderId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public AirTaxiOrder update(@PathParam("AirTaxiOrderId") String AirTaxiOrderId,
			@NotNull @Valid AirTaxiOrder newAirTaxiOrder) {
		return airTaxiOrderService.updateAirTaxiOrder(AirTaxiOrderId, newAirTaxiOrder);
	}

	/**
	 * Cancel order
	 */
	@POST
	@Path("{AirTaxiOrderId}/cancel")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public AirTaxiOrder cancelOrder(@PathParam("AirTaxiOrderId") String AirTaxiOrderId) {
		return airTaxiOrderService.updateAirTaxiOrderStatus(AirTaxiOrderId, Order.Status.CANCELLED);
	}

	/**
	 * Finish order
	 */
	@POST
	@Path("{AirTaxiOrderId}/finish")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public AirTaxiOrder finishOrder(@PathParam("AirTaxiOrderId") String AirTaxiOrderId) {
		return airTaxiOrderService.updateAirTaxiOrderStatus(AirTaxiOrderId, Order.Status.FINISHED);
	}

	/**
	 * Delete
	 */
	@DELETE
	@Path("{AirTaxiOrderId}")
	public Response delete(@PathParam("AirTaxiOrderId") String AirTaxiOrderId) {
		airTaxiOrderService.deleteAirTaxiOrder(AirTaxiOrderId);
		return Response.noContent().build();
	}

	/**
	 * Delete all
	 */
	@DELETE
	public Response deleteAll(@PathParam("userId") String userId) {
		airTaxiOrderService.deleteAirTaxiOrders(userId);
		return Response.noContent().build();
	}

}
