package net.aircommunity.platform.rest.tenant.order;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.FerryFlightOrder;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.order.FerryFlightOrderService;
import net.aircommunity.platform.service.order.OrderService;

/**
 * Tenant FerryFlightOrder RESTful API for ADMIN/TENANT
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT, Roles.ROLE_CUSTOMER_SERVICE })
public class TenantFerryFlightOrderResource extends TenantBaseOrderResourceSupport<FerryFlightOrder> {

	@Resource
	private FerryFlightOrderService ferryFlightOrderService;

	@Override
	protected OrderService<FerryFlightOrder> getOrderService() {
		return ferryFlightOrderService;
	}
}
