package net.aircommunity.platform.rest;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.aircommunity.platform.common.net.HttpHeaders;
import net.aircommunity.platform.model.AirTaxi;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.service.AirTaxiService;
import net.aircommunity.rest.annotation.RESTful;

/**
 * AirTaxi RESTful API allows list/find/query for ANYONE.
 * 
 * Created by guankai on 15/04/2017.
 */
@RESTful

@Path("taxis")
public class AirTaxiResource {
	private static final Logger LOG = LoggerFactory.getLogger(AirTaxiResource.class);

	@Resource
	private AirTaxiService airTaxiService;

	// ***********************
	// ANYONE
	// ***********************

	/**
	 * List all TODO query by
	 */
	@GET
	@PermitAll
	@Produces(MediaType.APPLICATION_JSON)
	public Response listAll(@QueryParam("departure") String departure, @QueryParam("page") int page,
			@QueryParam("pageSize") int pageSize) {
		LOG.debug("list all air taxi with departure: {}", departure);
		Page<AirTaxi> result = Page.emptyPage(page, pageSize);
		if (departure == null) {
			result = airTaxiService.listAirTaxis(page, pageSize);
		}
		else {
			result = airTaxiService.listAirTaxisByDeparture(departure, page, pageSize);
		}
		return Response.ok(result).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(result)).build();
	}

	/**
	 * Find
	 */
	@GET
	@PermitAll
	@Path("{airTaxiId}")
	@Produces(MediaType.APPLICATION_JSON)
	public AirTaxi find(@PathParam("airTaxiId") String airTaxiId) {
		return airTaxiService.findAirTaxi(airTaxiId);
	}

}
