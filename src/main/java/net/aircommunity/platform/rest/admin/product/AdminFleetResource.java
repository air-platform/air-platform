package net.aircommunity.platform.rest.admin.product;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.Fleet;
import net.aircommunity.platform.service.product.FleetService;
import net.aircommunity.platform.service.product.StandardProductService;

/**
 * Fleet RESTful API for ADMIN
 * 
 * @author Bin.Zhang
 */
@RESTful
@RolesAllowed(Roles.ROLE_ADMIN)
public class AdminFleetResource extends AdminProductResourceSupport<Fleet> {

	@Resource
	private FleetService fleetService;

	@Override
	protected StandardProductService<Fleet> getProductService() {
		return fleetService;
	}
}
