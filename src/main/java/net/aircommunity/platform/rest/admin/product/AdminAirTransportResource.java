package net.aircommunity.platform.rest.admin.product;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.AirTransport;
import net.aircommunity.platform.service.product.AirTransportService;
import net.aircommunity.platform.service.product.StandardProductService;

/**
 * AirTransport RESTful for ADMIN
 * 
 * @author Bin.Zhang
 */
@RESTful
@RolesAllowed(Roles.ROLE_ADMIN)
public class AdminAirTransportResource extends AdminProductResourceSupport<AirTransport> {

	@Resource
	private AirTransportService airTransportService;

	@Override
	protected StandardProductService<AirTransport> getProductService() {
		return airTransportService;
	}
}
