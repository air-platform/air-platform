package net.aircommunity.platform.rest;

import java.util.Set;

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

import com.fasterxml.jackson.annotation.JsonView;

import io.micro.annotation.RESTful;
import io.swagger.annotations.Api;
import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.AirTour;
import net.aircommunity.platform.service.AirTourService;

/**
 * AirTour RESTful API allows list/find/query for ANYONE.
 * 
 * @author guankai
 */
@Api
@RESTful
@PermitAll
@Path("tours")
public class AirTourResourse extends ProductResourceSupport<AirTour> {
	private static final Logger LOG = LoggerFactory.getLogger(AirTourResourse.class);

	@Resource
	private AirTourService airTourService;

	// ***********************
	// ANYONE
	// ***********************

	/**
	 * List all cities
	 */
	@GET
	@Path("flight/cities")
	@Produces(MediaType.APPLICATION_JSON)
	public Set<String> listAllCities() {
		LOG.debug("List all air tour cities");
		return airTourService.listAirTourCities();
	}

	/**
	 * List all
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Public.class)
	public Page<AirTour> listAll(@QueryParam("city") String city, @QueryParam("page") @DefaultValue("0") int page,
			@QueryParam("pageSize") @DefaultValue("0") int pageSize) {
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
	@Path("{airTourId}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Public.class)
	public AirTour find(@PathParam("airTourId") String airTourId) {
		return airTourService.findAirTour(airTourId);
	}

}
