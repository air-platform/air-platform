package net.aircommunity.platform.rest.tenant;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.common.net.HttpHeaders;
import net.aircommunity.platform.model.FerryFlightOrder;
import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.FerryFlightOrderService;

/**
 * Tenant FerryFlightOrder RESTful API. NOTE: <b>all permission</b> for ADMIN/TENANT
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT })
public class TenantFerryFlightOrderResource {
	private static final Logger LOG = LoggerFactory.getLogger(TenantFerryFlightOrderResource.class);

	@Resource
	private FerryFlightOrderService ferryFlightOrderService;

	/**
	 * Find
	 */
	@GET
	@Path("{orderId}")
	@Produces(MediaType.APPLICATION_JSON)
	public FerryFlightOrder find(@PathParam("orderId") String orderId) {
		return ferryFlightOrderService.findFerryFlightOrder(orderId);
	}

	/**
	 * List
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response list(@PathParam("tenantId") String tenantId, @QueryParam("status") String status,
			@QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		LOG.debug("List all orders with  status: {} for tenant: {} ", status, tenantId);
		Page<FerryFlightOrder> result = ferryFlightOrderService.listTenantFerryFlightOrders(tenantId,
				Order.Status.of(status), page, pageSize);
		return Response.ok(result).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(result)).build();
	}

	/**
	 * Mark order as Paid
	 */
	@POST
	@Path("{orderId}/paid")
	public Response markOrderPaid(@PathParam("orderId") String orderId) {
		ferryFlightOrderService.updateFerryFlightOrderStatus(orderId, Order.Status.PAID);
		return Response.noContent().build();
	}

}
