package net.aircommunity.platform.rest.admin.product;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.FerryFlight;
import net.aircommunity.platform.service.product.FerryFlightService;
import net.aircommunity.platform.service.product.StandardProductService;

/**
 * FerryFlight RESTful API for ADMIN
 * 
 * @author Bin.Zhang
 */
@RESTful
@RolesAllowed(Roles.ROLE_ADMIN)
public class AdminFerryFlightResource extends AdminProductResourceSupport<FerryFlight> {

	@Resource
	private FerryFlightService ferryFlightService;

	@Override
	protected StandardProductService<FerryFlight> getProductService() {
		return ferryFlightService;
	}
}
