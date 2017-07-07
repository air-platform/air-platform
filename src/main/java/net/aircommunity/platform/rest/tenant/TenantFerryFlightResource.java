package net.aircommunity.platform.rest.tenant;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.FerryFlight;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.product.FerryFlightService;
import net.aircommunity.platform.service.product.StandardProductService;

/**
 * FerryFlight RESTful API for ADMIN/TENANT
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT, Roles.ROLE_CUSTOMER_SERVICE })
public class TenantFerryFlightResource extends TenantProductResourceSupport<FerryFlight> {

	@Resource
	private FerryFlightService ferryFlightService;

	@Override
	protected StandardProductService<FerryFlight> getProductService() {
		return ferryFlightService;
	}
}
