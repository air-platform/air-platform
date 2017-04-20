package net.aircommunity.platform.rest;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micro.annotation.RESTful;
import io.swagger.annotations.Api;
import net.aircommunity.platform.common.net.HttpHeaders;
import net.aircommunity.platform.model.FerryFlight;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.service.FerryFlightService;

/**
 * FerryFlight RESTful API allows list/find/query for ANYONE
 * 
 * @author Bin.Zhang
 */
@Api
@RESTful
@PermitAll
@Path("ferryflights")
public class FerryFlightResource {
	private static final Logger LOG = LoggerFactory.getLogger(FerryFlightResource.class);

	@Resource
	private FerryFlightService ferryFlightService;

	// ***********************
	// ANYONE
	// ***********************

	/**
	 * List all TODO query by departure/arrival/date/timeSlot
	 */
	@GET
	@PermitAll
	@Produces(MediaType.APPLICATION_JSON)
	public Response listAll(@QueryParam("recommended") boolean recommended, @QueryParam("departure") String departure,
			@QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		if (recommended) {
			LOG.debug("List recommended ferryFlights for departure: {}", departure);
			return Response.ok(ferryFlightService.listTop3FerryFlights(departure)).build();
		}
		LOG.debug("List all ferryFlights for departure: {}", departure);
		Page<FerryFlight> result = ferryFlightService.listFerryFlightsByDeparture(departure, page, pageSize);
		return Response.ok(result).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(result)).build();
	}

	/**
	 * Find one
	 */
	@GET
	@PermitAll
	@Path("{ferryFlightId}")
	@Produces(MediaType.APPLICATION_JSON)
	public FerryFlight find(@PathParam("ferryFlightId") String ferryFlightId) {
		LOG.debug("Find ferryFlight: {}", ferryFlightId);
		return ferryFlightService.findFerryFlight(ferryFlightId);
	}

}
