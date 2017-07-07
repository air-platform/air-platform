package net.aircommunity.platform.rest.tenant;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.JetTravel;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.product.JetTravelService;
import net.aircommunity.platform.service.product.StandardProductService;

/**
 * Jet Travel RESTful API for ADMIN/TENANT
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT, Roles.ROLE_CUSTOMER_SERVICE })
public class TenantJetTravelResource extends TenantProductResourceSupport<JetTravel> {

	@Resource
	private JetTravelService jetTravelService;

	@Override
	protected StandardProductService<JetTravel> getProductService() {
		return jetTravelService;
	}
}
