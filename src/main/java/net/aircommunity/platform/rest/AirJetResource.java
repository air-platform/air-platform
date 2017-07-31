package net.aircommunity.platform.rest;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.annotation.JsonView;

import io.micro.annotation.RESTful;
import io.swagger.annotations.Api;
import net.aircommunity.platform.model.JsonViews;
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

	@Resource
	private AirJetService airJetService;

	// ***********************
	// ANYONE
	// ***********************

	/**
	 * List all
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Public.class)
	public Response listAll() {
		return Response.ok(airJetService.listAirJets()).build();
	}

	/**
	 * Find
	 */
	@GET
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
	@Path("type/{type}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Public.class)
	public AirJet findByType(@PathParam("type") String type) {
		return airJetService.findAirJetByType(type);
	}
}
