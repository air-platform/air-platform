package net.aircommunity.platform.rest.tenant;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.AirTransport;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.product.AirTransportService;
import net.aircommunity.platform.service.product.StandardProductService;

/**
 * AirTransport RESTful API for ADMIN/TENANT
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT, Roles.ROLE_CUSTOMER_SERVICE })
public class TenantAirTransportResource extends TenantProductResourceSupport<AirTransport> {

	@Resource
	private AirTransportService airTransportService;

	@Override
	protected StandardProductService<AirTransport> getProductService() {
		return airTransportService;
	}
}
