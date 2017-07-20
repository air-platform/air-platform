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
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonView;

import io.micro.annotation.RESTful;
import io.swagger.annotations.Api;
import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.Apron;
import net.aircommunity.platform.service.ApronService;

/**
 * Apron RESTful API allows list/find/query for ANYONE.
 * 
 * @author Bin.Zhang
 */
@Api
@RESTful
@PermitAll
@Path("aprons")
public class ApronResource {
	private static final Logger LOG = LoggerFactory.getLogger(ApronResource.class);

	@Resource
	private ApronService apronService;

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
	public Response listAll(@QueryParam("city") String city, @QueryParam("page") @DefaultValue("0") int page,
			@QueryParam("pageSize") @DefaultValue("0") int pageSize, @Context SecurityContext context) {
		LOG.debug("List all aprons with page: {} pageSize: {}", page, pageSize);
		// for ADMIN with pagination and without filtered by published
		if (context.isUserInRole(Roles.ROLE_ADMIN)) {
			return Response.ok(apronService.listAprons(page, pageSize)).build();
		}
		return Response.ok(apronService.listPublishedAprons(city)).build();
	}

	/**
	 * Find
	 */
	@GET
	@PermitAll
	@Path("{apronId}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Public.class)
	public Apron find(@PathParam("apronId") String apronId) {
		return apronService.findApron(apronId);
	}

	// *************
	// ADMIN ONLY
	// *************
	// XXX
	// NOTE: JsonView should be also applied to GET(none admin resources), once the Json view is enabled on Apron model
	// We haven't used any @JsonView on Apron model, so @JsonView will just ignored

	/**
	 * Create
	 */
	@POST
	@RolesAllowed(Roles.ROLE_ADMIN)
	@Consumes(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Admin.class)
	public Response create(@NotNull @Valid Apron Apron, @Context UriInfo uriInfo) {
		Apron created = apronService.createApron(Apron);
		URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
		LOG.debug("Created {}", uri);
		return Response.created(uri).build();
	}

	/**
	 * Update
	 */
	@PUT
	@Path("{apronId}")
	@RolesAllowed(Roles.ROLE_ADMIN)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Admin.class)
	public Apron update(@PathParam("apronId") String apronId, @NotNull @Valid Apron Apron) {
		return apronService.updateApron(apronId, Apron);
	}

	/**
	 * Delete
	 */
	@DELETE
	@Path("{apronId}")
	@RolesAllowed(Roles.ROLE_ADMIN)
	public void delete(@PathParam("apronId") String apronId) {
		apronService.deleteApron(apronId);
	}

	/**
	 * Delete all
	 */
	@DELETE
	@RolesAllowed(Roles.ROLE_ADMIN)
	public void deleteAll() {
		apronService.deleteAprons();
	}

}
