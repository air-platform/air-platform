package net.aircommunity.platform.rest;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonView;

import io.micro.annotation.RESTful;
import io.swagger.annotations.Api;
import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.JetTravel;
import net.aircommunity.platform.service.product.JetTravelService;
import net.aircommunity.platform.service.product.StandardProductService;

/**
 * Jet Travel RESTful API allows list/find/query for ANYONE
 * 
 * @author Bin.Zhang
 */
@Api
@RESTful
@PermitAll
@Path("jettravels")
public class JetTravelResource extends ProductResourceSupport<JetTravel> {
	private static final Logger LOG = LoggerFactory.getLogger(JetTravelResource.class);

	@Resource
	private JetTravelService jetTravelService;

	// ***********************
	// ANYONE
	// ***********************

	/**
	 * List all
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Public.class)
	public Page<JetTravel> listAll(@QueryParam("page") @DefaultValue("0") int page,
			@QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		LOG.debug("List all jet travels with page: {}, pageSize: {}", page, pageSize);
		return jetTravelService.listJetTravels(page, pageSize);
	}

	@Override
	protected StandardProductService<JetTravel> getProductService() {
		return jetTravelService;
	}
}
