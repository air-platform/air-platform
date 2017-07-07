package net.aircommunity.platform.rest.admin.product;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.AirTour;
import net.aircommunity.platform.service.product.AirTourService;
import net.aircommunity.platform.service.product.StandardProductService;

/**
 * AirTour RESTful API for ADMIN
 * 
 * @author Bin.Zhang
 */
@RESTful
@RolesAllowed(Roles.ROLE_ADMIN)
public class AdminAirTourResourse extends AdminProductResourceSupport<AirTour> {

	@Resource
	private AirTourService airTourService;

	@Override
	protected StandardProductService<AirTour> getProductService() {
		return airTourService;
	}
}
