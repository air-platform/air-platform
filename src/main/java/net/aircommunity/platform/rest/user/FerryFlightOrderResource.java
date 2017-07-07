package net.aircommunity.platform.rest.user;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.FerryFlightOrder;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.order.FerryFlightOrderService;
import net.aircommunity.platform.service.order.StandardOrderService;

/**
 * FerryFlightOrder RESTful API for ADMIN and USER.
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_USER })
public class FerryFlightOrderResource extends UserOrderResourceSupport<FerryFlightOrder> {

	@Resource
	private FerryFlightOrderService ferryFlightOrderService;

	@Override
	protected StandardOrderService<FerryFlightOrder> getStandardOrderService() {
		return ferryFlightOrderService;
	}
}
