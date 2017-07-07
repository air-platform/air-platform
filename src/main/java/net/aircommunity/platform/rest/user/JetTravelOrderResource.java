package net.aircommunity.platform.rest.user;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.JetTravelOrder;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.order.JetTravelOrderService;
import net.aircommunity.platform.service.order.StandardOrderService;

/**
 * JetTravelOrder RESTful API for ADMIN and USER.
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_USER })
public class JetTravelOrderResource extends UserOrderResourceSupport<JetTravelOrder> {

	@Resource
	private JetTravelOrderService jetTravelOrderService;

	@Override
	protected StandardOrderService<JetTravelOrder> getStandardOrderService() {
		return jetTravelOrderService;
	}
}
