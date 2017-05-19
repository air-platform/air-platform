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

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.AirTaxiOrder;
import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.rest.BaseOrderResource;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.AirTaxiOrderService;

/**
 * AirTaxiOrder RESTful API. <b>all permission</b> for ADMIN/TENANT
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT })
public class TenantAirTaxiOrderResource extends BaseOrderResource<AirTaxiOrder> {
	private static final Logger LOG = LoggerFactory.getLogger(TenantAirTaxiOrderResource.class);

	@Resource
	private AirTaxiOrderService airTaxiOrderService;

	/**
	 * List
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Page<AirTaxiOrder> list(@PathParam("tenantId") String tenantId, @QueryParam("status") Order.Status status,
			@QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		LOG.debug("List all orders with  status: {} for tenant: {} ", status, tenantId);
		return airTaxiOrderService.listTenantAirTaxiOrders(tenantId, status, page, pageSize);
	}

}
