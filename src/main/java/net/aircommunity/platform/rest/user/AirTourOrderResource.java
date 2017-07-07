package net.aircommunity.platform.rest.user;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.AirTourOrder;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.order.AirTourOrderService;
import net.aircommunity.platform.service.order.StandardOrderService;

/**
 * AirTourOrder RESTful API for ADMIN and USER.
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_USER })
public class AirTourOrderResource extends UserOrderResourceSupport<AirTourOrder> {

	@Resource
	private AirTourOrderService airTourOrderService;

	@Override
	protected StandardOrderService<AirTourOrder> getStandardOrderService() {
		return airTourOrderService;
	}
}
