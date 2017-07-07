package net.aircommunity.platform.rest.admin.order;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.annotation.JsonView;

import io.micro.annotation.RESTful;
import io.micro.common.Strings;
import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.rest.tenant.order.TenantOrderResourceSupport;
import net.aircommunity.platform.service.order.CommonOrderService;
import net.aircommunity.platform.service.order.OrderService;

/**
 * Order RESTful API for ADMIN
 * 
 * @author Bin.Zhang
 */
@RESTful
@RolesAllowed(Roles.ROLE_ADMIN)
public class AdminOrderResource extends TenantOrderResourceSupport<Order> {

	@Resource
	private CommonOrderService commonOrderService;

	// **************
	// ADMIN ONLY
	// **************

	/**
	 * List All
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Admin.class)
	public Page<Order> listAllOrders(@QueryParam("user") String userId, @QueryParam("status") Order.Status status,
			@QueryParam("type") Product.Type type, @QueryParam("page") @DefaultValue("0") int page,
			@QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		if (Strings.isBlank(userId)) {
			return getOrderService().listAllOrders(status, type, page, pageSize);
		}
		return getOrderService().listAllUserOrders(userId, status, type, page, pageSize);
	}

	/**
	 * Delete
	 */
	@DELETE
	@Path("{orderId}/force")
	public void forceDelete(@PathParam("orderId") String orderId) {
		getOrderService().deleteOrder(orderId);
	}

	/**
	 * Delete all
	 */
	@DELETE
	public void deleteAll(@PathParam("userId") String userId) {
		getOrderService().deleteOrders(userId);
	}

	@Override
	protected OrderService<Order> getOrderService() {
		return commonOrderService;
	}

}
