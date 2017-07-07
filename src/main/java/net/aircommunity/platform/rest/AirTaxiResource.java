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
import net.aircommunity.platform.model.domain.AirTaxi;
import net.aircommunity.platform.service.product.AirTaxiService;
import net.aircommunity.platform.service.product.StandardProductService;

/**
 * AirTaxi RESTful API allows list/find/query for ANYONE.
 * 
 * @author guankai
 */
@Api
@RESTful
@PermitAll
@Path("taxis")
public class AirTaxiResource extends ProductResourceSupport<AirTaxi> {
	private static final Logger LOG = LoggerFactory.getLogger(AirTaxiResource.class);

	@Resource
	private AirTaxiService airTaxiService;

	// ***********************
	// ANYONE
	// ***********************

	/**
	 * List all (query: departure=xxx&arrival=xxx&provider=xxx)
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Public.class)
	public Page<AirTaxi> listAll(@QueryParam("departure") String departure, @QueryParam("arrival") String arrival,
			@QueryParam("provider") String tenantId, @QueryParam("page") @DefaultValue("0") int page,
			@QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		LOG.debug("List all air taxis for  departure: {}, arrival: {}, tenant: {}", departure, arrival, tenantId);
		return airTaxiService.listAirTaxisWithConditions(departure, arrival, tenantId, page, pageSize);
	}

	@Override
	protected StandardProductService<AirTaxi> getProductService() {
		return airTaxiService;
	}

}
