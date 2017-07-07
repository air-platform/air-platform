package net.aircommunity.platform.rest.tenant;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.Fleet;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.product.FleetService;
import net.aircommunity.platform.service.product.StandardProductService;

/**
 * Fleet RESTful API for ADMIN/TENANT
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT, Roles.ROLE_CUSTOMER_SERVICE })
public class TenantFleetResource extends TenantProductResourceSupport<Fleet> {

	@Resource
	private FleetService fleetService;

	@Override
	protected StandardProductService<Fleet> getProductService() {
		return fleetService;
	}
}
