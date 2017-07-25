package net.aircommunity.platform.rest.user;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.QuickFlightOrder;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.order.QuickFlightOrderService;
import net.aircommunity.platform.service.order.StandardOrderService;

/**
 * QuickFlightOrder RESTful API for ADMIN and USER.
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_USER })
public class QuickFlightOrderResource extends UserOrderResourceSupport<QuickFlightOrder> {

	@Resource
	private QuickFlightOrderService quickFlightOrderService;

	@Override
	protected StandardOrderService<QuickFlightOrder> getStandardOrderService() {
		return quickFlightOrderService;
	}
}
