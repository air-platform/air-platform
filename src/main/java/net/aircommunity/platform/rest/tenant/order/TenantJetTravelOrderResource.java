package net.aircommunity.platform.rest.tenant.order;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.JetTravelOrder;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.order.JetTravelOrderService;
import net.aircommunity.platform.service.order.OrderService;

/**
 * Tenant JetTravelOrder RESTful API for ADMIN/TENANT
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT, Roles.ROLE_CUSTOMER_SERVICE })
public class TenantJetTravelOrderResource extends TenantBaseOrderResourceSupport<JetTravelOrder> {

	@Resource
	private JetTravelOrderService jetTravelOrderService;

	@Override
	protected OrderService<JetTravelOrder> getOrderService() {
		return jetTravelOrderService;
	}

}
