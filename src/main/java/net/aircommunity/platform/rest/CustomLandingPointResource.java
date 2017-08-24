package net.aircommunity.platform.rest;

import com.fasterxml.jackson.annotation.JsonView;
import io.micro.annotation.RESTful;
import io.swagger.annotations.Api;
import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.CustomLandingPoint;
import net.aircommunity.platform.service.CustomLandingPointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

/**
 * Apron RESTful API allows list/find/query for ANYONE.
 *
 * @author Bin.Zhang
 */
@Api
@RESTful
@PermitAll
@Path("customLandingPoints")
public class CustomLandingPointResource {
	private static final Logger LOG = LoggerFactory.getLogger(CustomLandingPointResource.class);

	@Resource
	private CustomLandingPointService customLandingPointService;

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
	public Response listAll(@QueryParam("page") @DefaultValue("1") int page,
							@QueryParam("pageSize") @DefaultValue("10") int pageSize) {
		return Response.ok(customLandingPointService.listCustomLandingPoints(page, pageSize)).build();
	}


	/**
	 * Find
	 */
	@GET
	@PermitAll
	@Path("{customLandingPointId}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Public.class)
	public CustomLandingPoint find(@PathParam("customLandingPointId") String customLandingPointId) {
		return customLandingPointService.findCustomLandingPoint(customLandingPointId);
	}

	// *************
	// USER ONLY
	// *************
	// XXX
	// NOTE: JsonView should be also applied to GET(none admin resources), once the Json view is enabled on Apron model
	// We haven't used any @JsonView on Apron model, so @JsonView will just ignored

	/**
	 * Create
	 */
	@POST
	@RolesAllowed(Roles.ROLE_USER)
	@Consumes(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.User.class)
	public Response create(@NotNull @Valid CustomLandingPoint CustomLandingPoint, @Context UriInfo uriInfo) {
		CustomLandingPoint created = customLandingPointService.createCustomLandingPoint(CustomLandingPoint);
		URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
		LOG.debug("Created {}", uri);
		return Response.created(uri).build();
	}

	/**
	 * Update
	 */
	@PUT
	@Path("{customLandingPointId}")
	@RolesAllowed(Roles.ROLE_USER)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.User.class)
	public CustomLandingPoint update(@PathParam("customLandingPointId") String customLandingPointId, @NotNull @Valid CustomLandingPoint CustomLandingPoint) {
		return customLandingPointService.updateCustomLandingPoint(customLandingPointId, CustomLandingPoint);
	}

	/**
	 * Delete
	 */
	@DELETE
	@Path("{customLandingPointService}")
	@RolesAllowed(Roles.ROLE_USER)
	public void delete(@PathParam("customLandingPointId") String customLandingPointId) {
		customLandingPointService.deleteCustomLandingPoint(customLandingPointId);
	}

	/**
	 * Delete all
	 */
	@DELETE
	@RolesAllowed(Roles.ROLE_USER)
	public void deleteAll() {
		customLandingPointService.deleteCustomLandingPoints();
	}

}
