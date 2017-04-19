package net.aircommunity.platform.rest;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import net.aircommunity.platform.common.net.HttpHeaders;
import net.aircommunity.platform.model.AirJet;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.service.AirJetService;
import net.aircommunity.rest.annotation.RESTful;

/**
 * AirJet RESTful API allows list/find/query for ANYONE.
 * 
 * @author Bin.Zhang
 */
@RESTful
@PermitAll
@Path("airjets")
@Api("airjets")
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
	public Response listAll(@QueryParam("page") @DefaultValue("0") int page,
			@QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		LOG.debug("list all airJets");
		if (page > 0) {
			Page<AirJet> result = airJetService.listAirJets(page, pageSize);
			return Response.ok(result).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(result)).build();
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
	public AirJet findByType(@PathParam("type") String type) {
		return airJetService.findAirJetByType(type);
	}

	// *************
	// ADMIN ONLY
	// *************
	/**
	 * Update
	 */
	@PUT
	@Path("{airJetId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed(Roles.ROLE_ADMIN)
	public AirJet update(@PathParam("airJetId") String airJetId, @NotNull @Valid AirJet newAirJet) {
		return airJetService.updateAirJet(airJetId, newAirJet);
	}

	/**
	 * Delete
	 */
	@DELETE
	@Path("{airJetId}")
	@RolesAllowed(Roles.ROLE_ADMIN)
	public Response delete(@PathParam("airJetId") String airJetId) {
		airJetService.deleteAirJet(airJetId);
		return Response.noContent().build();
	}

	/**
	 * Delete all
	 */
	@DELETE
	@RolesAllowed(Roles.ROLE_ADMIN)
	public Response deleteAll() {
		airJetService.deleteAirJets();
		return Response.noContent().build();
	}

}
