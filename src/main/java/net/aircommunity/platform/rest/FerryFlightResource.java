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

import com.fasterxml.jackson.annotation.JsonView;

import io.micro.annotation.RESTful;
import io.micro.common.Strings;
import io.swagger.annotations.Api;
import net.aircommunity.platform.model.FerryFlight;
import net.aircommunity.platform.model.JsonViews;
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
public class FerryFlightResource extends ProductResourceSupport<FerryFlight> {
	private static final Logger LOG = LoggerFactory.getLogger(FerryFlightResource.class);

	@Resource
	private FerryFlightService ferryFlightService;

	// ***********************
	// ANYONE
	// ***********************

	/**
	 * List all query by departure/arrival
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Public.class)
	public Response listAll(@QueryParam("recommended") boolean recommended, @QueryParam("departure") String departure,
			@QueryParam("arrival") String arrival, @QueryParam("page") @DefaultValue("0") int page,
			@QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		if (recommended) {
			LOG.debug("List recommended ferryFlights for departure: {}", departure);
			return Response.ok(ferryFlightService.listTop3FerryFlights(departure)).build();
		}
		// by departure
		if (Strings.isNotBlank(departure)) {
			LOG.debug("List all ferryFlights for departure: {}", departure);
			return Response.ok(ferryFlightService.listFerryFlightsByDeparture(departure, page, pageSize)).build();
		}
		// by arrival
		if (Strings.isNotBlank(arrival)) {
			LOG.debug("List all ferryFlights for arrival: {}", arrival);
			return Response.ok(ferryFlightService.listFerryFlightsByArrival(arrival, page, pageSize)).build();
		}
		// all
		return Response.ok(ferryFlightService.listFerryFlights(page, pageSize)).build();
	}

	/**
	 * Find one
	 */
	@GET
	@Path("{ferryFlightId}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Public.class)
	public FerryFlight find(@PathParam("ferryFlightId") String ferryFlightId) {
		return ferryFlightService.findFerryFlight(ferryFlightId);
	}

}
