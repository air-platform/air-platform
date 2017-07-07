package net.aircommunity.platform.rest.user;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.CharterOrder;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.order.CharterOrderService;
import net.aircommunity.platform.service.order.StandardOrderService;

/**
 * CharterOrder RESTful API for ADMIN and USER.
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_USER })
public class CharterOrderResource extends UserOrderResourceSupport<CharterOrder> {

	@Resource
	private CharterOrderService charterOrderService;

	@Override
	protected StandardOrderService<CharterOrder> getStandardOrderService() {
		return charterOrderService;
	}
}
