package net.aircommunity.platform.rest.tenant;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.AirTaxi;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.product.AirTaxiService;
import net.aircommunity.platform.service.product.StandardProductService;

/**
 * AirTour RESTful API for ADMIN/TENANT
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT, Roles.ROLE_CUSTOMER_SERVICE })
public class TenantAirTaxiResource extends TenantProductResourceSupport<AirTaxi> {

	@Resource
	private AirTaxiService airTaxiService;

	@Override
	protected StandardProductService<AirTaxi> getProductService() {
		return airTaxiService;
	}
}
