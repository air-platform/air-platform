package net.aircommunity.platform.rest.tenant.order;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.AirTransportOrder;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.order.AirTransportOrderService;
import net.aircommunity.platform.service.order.OrderService;

/**
 * AirTransportOrder RESTful API for ADMIN/TENANT
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT, Roles.ROLE_CUSTOMER_SERVICE })
public class TenantAirTransportOrderResource extends TenantBaseOrderResourceSupport<AirTransportOrder> {

	@Resource
	private AirTransportOrderService airTransportOrderService;

	@Override
	protected OrderService<AirTransportOrder> getOrderService() {
		return airTransportOrderService;
	}
}
