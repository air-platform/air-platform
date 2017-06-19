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
import io.swagger.annotations.Api;
import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.AirClass;
import net.aircommunity.platform.service.AirClassService;

/**
 * AirClass RESTful API allows list/find/query for ANYONE.
 * 
 * @author Bin.Zhang
 */
@Api
@RESTful
@PermitAll
@Path("airclasses")
public class AirClassResource {
	private static final Logger LOG = LoggerFactory.getLogger(AirClassResource.class);

	@Resource
	private AirClassService airClassService;

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
	public Page<AirClass> listAll(@QueryParam("page") @DefaultValue("0") int page,
			@QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		LOG.debug("List all AirClasss with page: {} pageSize: {}", page, pageSize);
		return airClassService.listAirClasses(page, pageSize);
	}

	/**
	 * Find
	 */
	@GET
	@PermitAll
	@Path("{airClassId}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Public.class)
	public AirClass find(@PathParam("airClassId") String airClassId) {
		return airClassService.findAirClass(airClassId);
	}

	// *************
	// ADMIN ONLY
	// *************
	// XXX
	// NOTE: JsonView should be also applied to GET(none admin resources), once the Json view is enabled on AirClass
	// model
	// We haven't used any @JsonView on AirClass model, so @JsonView will just ignored

	/**
	 * Create
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@RolesAllowed(Roles.ROLE_ADMIN)
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	public Response create(@NotNull @Valid AirClass AirClass, @Context UriInfo uriInfo) {
		AirClass created = airClassService.createAirClass(AirClass);
		URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
		LOG.debug("Created {}", uri);
		return Response.created(uri).build();
	}

	/**
	 * Update
	 */
	@PUT
	@Path("{airClassId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed(Roles.ROLE_ADMIN)
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	public AirClass update(@PathParam("airClassId") String airClassId, @NotNull @Valid AirClass newAirClass) {
		return airClassService.updateAirClass(airClassId, newAirClass);
	}

	/**
	 * Delete
	 */
	@DELETE
	@Path("{airClassId}")
	@RolesAllowed(Roles.ROLE_ADMIN)
	public void delete(@PathParam("airClassId") String airClassId) {
		airClassService.deleteAirClass(airClassId);
	}

	/**
	 * Delete all
	 */
	@DELETE
	@RolesAllowed(Roles.ROLE_ADMIN)
	public void deleteAll() {
		airClassService.deleteAirClasses();
	}

}
