package net.aircommunity.platform.rest.admin.product;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.AirTaxi;
import net.aircommunity.platform.service.product.AirTaxiService;
import net.aircommunity.platform.service.product.StandardProductService;

/**
 * AirTour RESTful API for ADMIN
 */
@RESTful
@RolesAllowed({ Roles.ROLE_ADMIN })
public class AdminAirTaxiResource extends AdminProductResourceSupport<AirTaxi> {

	@Resource
	private AirTaxiService airTaxiService;

	@Override
	protected StandardProductService<AirTaxi> getProductService() {
		return airTaxiService;
	}
}
