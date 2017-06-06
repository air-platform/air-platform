package net.aircommunity.platform.rest;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonView;

import io.micro.annotation.RESTful;
import io.swagger.annotations.Api;
import net.aircommunity.platform.model.AirTaxi;
import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.service.AirTaxiService;

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
	 * List all
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Public.class)
	public Page<AirTaxi> listAll(@QueryParam("departure") String departure, @QueryParam("page") int page,
			@QueryParam("pageSize") int pageSize) {
		LOG.debug("list all air taxi with departure: {}", departure);
		if (departure == null) {
			return airTaxiService.listAirTaxis(page, pageSize);
		}
		return airTaxiService.listAirTaxisByDeparture(departure, page, pageSize);
	}

	/**
	 * Find
	 */
	@GET
	@Path("{airTaxiId}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Public.class)
	public AirTaxi find(@PathParam("airTaxiId") String airTaxiId) {
		return airTaxiService.findAirTaxi(airTaxiId);
	}

}
