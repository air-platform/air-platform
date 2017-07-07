package net.aircommunity.platform.rest.admin.product;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.JetTravel;
import net.aircommunity.platform.service.product.JetTravelService;
import net.aircommunity.platform.service.product.StandardProductService;

/**
 * Jet Travel RESTful API for ADMIN
 * 
 * @author Bin.Zhang
 */
@RESTful
@RolesAllowed(Roles.ROLE_ADMIN)
public class AdminJetTravelResource extends AdminProductResourceSupport<JetTravel> {

	@Resource
	private JetTravelService jetTravelService;

	@Override
	protected StandardProductService<JetTravel> getProductService() {
		return jetTravelService;
	}
}
