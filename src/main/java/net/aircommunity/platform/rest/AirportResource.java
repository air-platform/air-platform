package net.aircommunity.platform.rest;

import java.net.URI;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonView;

import io.micro.annotation.RESTful;
import io.micro.common.Strings;
import io.swagger.annotations.Api;
import net.aircommunity.platform.model.Airport;
import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.service.AirportService;

/**
 * Airport RESTful API allows list/find/query for ANYONE.
 * 
 * @author Bin.Zhang
 */
@Api
@RESTful
@PermitAll
@Path("airports")
public class AirportResource {
	private static final Logger LOG = LoggerFactory.getLogger(AirportResource.class);

	@Resource
	private AirportService airportService;

	// ***********************
	// ANYONE
	// ***********************

	/**
	 * List all
	 */
	@GET
	@PermitAll
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Public.class)
	public Response listAll(@QueryParam("name") String name, @QueryParam("city") String city,
			@QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		boolean hasName = Strings.isNotBlank(name);
		boolean hasCity = Strings.isNotBlank(city);
		if (hasName && hasCity) {
			LOG.debug("list airports by city: {} and name: {}", city, name);
			return Response.ok(airportService.listAirportsByCityAndName(city, name)).build();
		}
		if (hasName) {
			LOG.debug("list airports by name: {}", name);
			return Response.ok(airportService.listAirportsByName(name)).build();
		}
		if (hasCity) {
			LOG.debug("list airports by city: {}", city);
			return Response.ok(airportService.listAirportsByCity(city)).build();
		}
		LOG.debug("list all Airports");
		Page<Airport> result = airportService.listAirports(page, pageSize);
		return Response.ok(result).build();
	}

	/**
	 * Find
	 */
	@GET
	@PermitAll
	@Path("{airportId}")
	@JsonView(JsonViews.Public.class)
	@Produces(MediaType.APPLICATION_JSON)
	public Airport find(@PathParam("airportId") String airportId) {
		return airportService.findAirport(airportId);
	}

	/**
	 * Find by code (iata3 or icao4)
	 */
	@GET
	@PermitAll
	@Path("code/{code}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Public.class)
	public Airport findByCode(@PathParam("code") String code) {
		return airportService.findAirportByCode(code);
	}

	// *************
	// ADMIN ONLY
	// *************
	// XXX
	// NOTE: JsonView should be also applied to GET(none admin resources), once the Json view is enabled on Airport
	// model
	// We haven't used any @JsonView on Airport model, so @JsonView will just ignored

	/**
	 * Create
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@RolesAllowed(Roles.ROLE_ADMIN)
	@JsonView(JsonViews.Admin.class)
	public Response create(@NotNull @Valid Airport airport, @Context UriInfo uriInfo) {
		Airport created = airportService.createAirport(airport);
		URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
		LOG.debug("Created {}", uri);
		return Response.created(uri).build();
	}

	/**
	 * Update
	 */
	@PUT
	@Path("{airportId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed(Roles.ROLE_ADMIN)
	@JsonView(JsonViews.Admin.class)
	public Airport update(@PathParam("airportId") String airportId, @NotNull @Valid Airport newAirport) {
		return airportService.updateAirport(airportId, newAirport);
	}

	/**
	 * Delete
	 */
	@DELETE
	@Path("{airportId}")
	@RolesAllowed(Roles.ROLE_ADMIN)
	public void delete(@PathParam("airportId") String airportId) {
		airportService.deleteAirport(airportId);
	}

	/**
	 * Delete all
	 */
	@DELETE
	@RolesAllowed(Roles.ROLE_ADMIN)
	public void deleteAll() {
		airportService.deleteAllAirports();
	}

}
