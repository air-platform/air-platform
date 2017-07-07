package net.aircommunity.platform.rest.user;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.AirTransportOrder;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.order.AirTransportOrderService;
import net.aircommunity.platform.service.order.StandardOrderService;

/**
 * AirTransportOrder RESTful API for ADMIN and USER.
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_USER })
public class AirTransportOrderResource extends UserOrderResourceSupport<AirTransportOrder> {

	@Resource
	private AirTransportOrderService airTransportOrderService;

	@Override
	protected StandardOrderService<AirTransportOrder> getStandardOrderService() {
		return airTransportOrderService;
	}
}
