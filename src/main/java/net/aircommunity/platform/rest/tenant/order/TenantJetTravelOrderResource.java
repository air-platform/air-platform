package net.aircommunity.platform.rest.tenant.order;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonView;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.JetTravelOrder;
import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.JetTravelOrderService;

/**
 * Tenant JetTravelOrder RESTful API. NOTE: <b>all permission</b> for ADMIN/TENANT
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT })
public class TenantJetTravelOrderResource extends TenantBaseOrderResource<JetTravelOrder> {
	private static final Logger LOG = LoggerFactory.getLogger(TenantJetTravelOrderResource.class);

	@Resource
	private JetTravelOrderService jetTravelOrderService;

	/**
	 * List
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	public Page<JetTravelOrder> list(@PathParam("tenantId") String tenantId, @QueryParam("status") Order.Status status,
			@QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		LOG.debug("List all orders with  status: {} for tenant: {} ", status, tenantId);
		return jetTravelOrderService.listTenantJetTravelOrders(tenantId, status, page, pageSize);
	}

}
