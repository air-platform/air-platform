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
import net.aircommunity.platform.model.Aircraft;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.service.AircraftService;

/**
 * Aircraft RESTful API allows list/find/query for ANYONE.
 * 
 * @author Bin.Zhang
 */
@Api
@RESTful
@PermitAll
@Path("aircrafts")
public class AircraftResource {
	private static final Logger LOG = LoggerFactory.getLogger(AircraftResource.class);

	@Resource
	private AircraftService aircraftService;

	// ***********************
	// ANYONE
	// ***********************

	/**
	 * List all TODO query by
	 */
	@GET
	@PermitAll
	@Produces(MediaType.APPLICATION_JSON)
	public Page<Aircraft> listAll(@QueryParam("page") @DefaultValue("0") int page,
			@QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		LOG.debug("list all aircrafts");
		return aircraftService.listAircrafts(page, pageSize);
	}

	/**
	 * Find
	 */
	@GET
	@PermitAll
	@Path("{aircraftId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Aircraft find(@PathParam("aircraftId") String aircraftId) {
		return aircraftService.findAircraft(aircraftId);
	}

}
