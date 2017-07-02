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
import net.aircommunity.platform.model.domain.AirJet;
import net.aircommunity.platform.service.AirJetService;

/**
 * AirJet RESTful API allows list/find/query for ANYONE.
 * 
 * @author Bin.Zhang
 */
@Api
@RESTful
@PermitAll
@Path("airjets")
public class AirJetResource {
	private static final Logger LOG = LoggerFactory.getLogger(AirJetResource.class);

	@Resource
	private AirJetService airJetService;

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
	public Response listAll(@QueryParam("page") @DefaultValue("0") int page,
			@QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		LOG.debug("List all airJets with page: {} pageSize: {}", page, pageSize);
		if (page > 0) {
			Page<AirJet> result = airJetService.listAirJets(page, pageSize);
			return Response.ok(result).build();
		}
		return Response.ok(airJetService.listAirJets()).build();
	}

	/**
	 * Find
	 */
	@GET
	@PermitAll
	@Path("{airJetId}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Public.class)
	public AirJet find(@PathParam("airJetId") String airJetId) {
		return airJetService.findAirJet(airJetId);
	}

	/**
	 * Find by Type
	 */
	@GET
	@PermitAll
	@Path("type/{type}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Public.class)
	public AirJet findByType(@PathParam("type") String type) {
		return airJetService.findAirJetByType(type);
	}

	// *************
	// ADMIN ONLY
	// *************
	// XXX
	// NOTE: JsonView should be also applied to GET(none admin resources), once the Json view is enabled on AirJet model
	// We haven't used any @JsonView on AirJet model, so @JsonView will just ignored

	/**
	 * Create
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@RolesAllowed(Roles.ROLE_ADMIN)
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	public Response create(@NotNull @Valid AirJet airJet, @Context UriInfo uriInfo) {
		AirJet created = airJetService.createAirJet(airJet);
		URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
		LOG.debug("Created {}", uri);
		return Response.created(uri).build();
	}

	/**
	 * Update
	 */
	@PUT
	@Path("{airJetId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed(Roles.ROLE_ADMIN)
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	public AirJet update(@PathParam("airJetId") String airJetId, @NotNull @Valid AirJet airJet) {
		return airJetService.updateAirJet(airJetId, airJet);
	}

	/**
	 * Delete
	 */
	@DELETE
	@Path("{airJetId}")
	@RolesAllowed(Roles.ROLE_ADMIN)
	public void delete(@PathParam("airJetId") String airJetId) {
		airJetService.deleteAirJet(airJetId);
	}

	/**
	 * Delete all
	 */
	@DELETE
	@RolesAllowed(Roles.ROLE_ADMIN)
	public void deleteAll() {
		airJetService.deleteAirJets();
	}

}
