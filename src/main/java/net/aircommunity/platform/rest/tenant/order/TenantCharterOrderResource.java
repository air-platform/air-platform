package net.aircommunity.platform.rest.tenant.order;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonView;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.CharterOrder;
import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.CharterOrderService;

/**
 * Tenant CharterOrder RESTful API. NOTE: <b>all permission</b> for ADMIN/TENANT
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT })
public class TenantCharterOrderResource extends TenantBaseOrderResource<CharterOrder> {
	private static final Logger LOG = LoggerFactory.getLogger(TenantCharterOrderResource.class);

	@Resource
	private CharterOrderService charterOrderService;

	/**
	 * List
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	public Page<CharterOrder> list(@PathParam("tenantId") String tenantId, @QueryParam("status") Order.Status status,
			@QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		LOG.debug("List all orders with status: {} for tenant: {} ", status, tenantId);
		if (Order.Status.PUBLISHED == status) {
			return charterOrderService.listCharterOrders(status, page, pageSize);
		}
		return charterOrderService.listTenantCharterOrders(tenantId, status, page, pageSize);
	}

	/**
	 * Offer a fleet (only allow one to be offered)
	 */
	@POST
	@Path("{orderId}/offer")
	public void offerFleet(@PathParam("orderId") String orderId,
			@NotNull @QueryParam("candidate") String fleetCandidateId,
			@NotNull @QueryParam("totalPrice") double totalPrice) {
		charterOrderService.offerFleetCandidate(orderId, fleetCandidateId, totalPrice);
	}
}
