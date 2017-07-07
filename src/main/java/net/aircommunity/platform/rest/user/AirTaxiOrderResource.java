package net.aircommunity.platform.rest.user;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.AirTaxiOrder;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.order.AirTaxiOrderService;
import net.aircommunity.platform.service.order.StandardOrderService;

/**
 * AirTaxiOrder RESTful API for ADMIN and USER.
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_USER })
public class AirTaxiOrderResource extends UserOrderResourceSupport<AirTaxiOrder> {

	@Resource
	private AirTaxiOrderService airTaxiOrderService;

	@Override
	protected StandardOrderService<AirTaxiOrder> getStandardOrderService() {
		return airTaxiOrderService;
	}
}
