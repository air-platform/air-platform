package net.aircommunity.platform.rest.tenant.order;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.AirTourOrder;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.order.AirTourOrderService;
import net.aircommunity.platform.service.order.OrderService;

/**
 * AirTourOrder RESTful API for ADMIN/TENANT
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT, Roles.ROLE_CUSTOMER_SERVICE })
public class TenantAirTourOrderResource extends TenantBaseOrderResourceSupport<AirTourOrder> {

	@Resource
	private AirTourOrderService airTourOrderService;

	@Override
	protected OrderService<AirTourOrder> getOrderService() {
		return airTourOrderService;
	}
}
