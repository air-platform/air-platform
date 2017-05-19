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

import io.micro.annotation.RESTful;
import io.swagger.annotations.Api;
import net.aircommunity.platform.model.AirTour;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.service.AirTourService;

/**
 * AirTour RESTful API allows list/find/query for ANYONE.
 * 
 * Created by guankai on 15/04/2017.
 */
@Api
@RESTful
@PermitAll
@Path("tours")
public class AirTourResourse {
	private static final Logger LOG = LoggerFactory.getLogger(AirTourResourse.class);

	@Resource
	private AirTourService airTourService;

	// ***********************
	// ANYONE
	// ***********************

	/**
	 * List all TODO query by
	 */
	@GET
	@PermitAll
	@Produces(MediaType.APPLICATION_JSON)
	public Page<AirTour> listAll(@QueryParam("city") String city, @QueryParam("page") int page,
			@QueryParam("pageSize") int pageSize) {
		LOG.debug("list all air tours");
		if (city == null) {
			return airTourService.listAirTours(page, pageSize);
		}
		return airTourService.listAirToursByCity(city, page, pageSize);
	}

	/**
	 * Find
	 */
	@GET
	@PermitAll
	@Path("{airTourId}")
	@Produces(MediaType.APPLICATION_JSON)
	public AirTour find(@PathParam("airTourId") String airTourId) {
		return airTourService.findAirTour(airTourId);
	}

}
