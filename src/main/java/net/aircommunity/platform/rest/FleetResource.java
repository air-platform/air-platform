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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micro.annotation.RESTful;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.aircommunity.platform.model.Fleet;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.service.FleetService;

/**
 * Fleet RESTful API allows list/find/query for ANYONE
 * 
 * @author Bin.Zhang
 */
@Api
@RESTful
@PermitAll
@Path("fleets")
public class FleetResource {
	private static final Logger LOG = LoggerFactory.getLogger(FleetResource.class);

	@Resource
	private FleetService fleetService;

	// ***********************
	// ANYONE
	// ***********************

	/**
	 * List all TODO query
	 */
	@GET
	@PermitAll
	@ApiOperation(value = "List All Fleets", response = Fleet.class, responseContainer = "List")
	@Produces(MediaType.APPLICATION_JSON)
	public Page<Fleet> listAll(@QueryParam("type") String type, @QueryParam("page") @DefaultValue("0") int page,
			@QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		LOG.debug("List all fleets");
		if (type != null) {
			return fleetService.listFleetsByType(type, page, pageSize);
		}
		return fleetService.listFleets(page, pageSize);
	}

	/**
	 * Find
	 */
	@GET
	@PermitAll
	@Path("{fleetId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Fleet find(@PathParam("fleetId") String fleetId) {
		return fleetService.findFleet(fleetId);
	}

	/**
	 * Find by flightNo
	 */
	@GET
	@PermitAll
	@Path("query/flightno/{flightNo}")
	@Produces(MediaType.APPLICATION_JSON)
	public Fleet findFlightNo(@PathParam("flightNo") String flightNo) {
		return fleetService.findFleetByFlightNo(flightNo);
	}

}
