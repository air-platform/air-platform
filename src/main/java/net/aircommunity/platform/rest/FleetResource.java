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

import net.aircommunity.platform.common.net.HttpHeaders;
import net.aircommunity.platform.model.Fleet;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.service.FleetService;
import net.aircommunity.rest.annotation.RESTful;

/**
 * Fleet RESTful API allows list/find/query for ANYONE
 * 
 * @author Bin.Zhang
 */
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
	@Produces(MediaType.APPLICATION_JSON)
	public Response listAll(@QueryParam("type") String type, @QueryParam("page") @DefaultValue("0") int page,
			@QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		LOG.debug("List all fleets");
		Page<Fleet> result = Page.emptyPage(page, pageSize);
		if (type != null) {
			result = fleetService.listFleetsByType(type, page, pageSize);
		}
		else {
			result = fleetService.listFleets(page, pageSize);
		}
		return Response.ok(result).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(result)).build();
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

}
