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

import io.swagger.annotations.Api;
import net.aircommunity.platform.common.net.HttpHeaders;
import net.aircommunity.platform.model.FerryFlight;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.service.FerryFlightService;
import net.aircommunity.rest.annotation.RESTful;

/**
 * FerryFlight RESTful API allows list/find/query for ANYONE
 * 
 * @author Bin.Zhang
 */
@RESTful
@PermitAll
@Path("ferryflights")
@Api("ferryflights")
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
	public Response listAll(@QueryParam("page") @DefaultValue("0") int page,
			@QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		LOG.debug("List all ferryFlights");
		Page<FerryFlight> result = ferryFlightService.listFerryFlights(page, pageSize);
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
