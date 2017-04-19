package net.aircommunity.platform.rest;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.aircommunity.platform.common.net.HttpHeaders;
import net.aircommunity.platform.model.JetCard;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.service.JetCardService;
import net.aircommunity.rest.annotation.RESTful;

/**
 * Jet Card RESTful API allows list/find/query for ANYONE
 * 
 * @author Bin.Zhang
 */
@RESTful
@PermitAll
@Path("jetcards")
public class JetCardResource {
	private static final Logger LOG = LoggerFactory.getLogger(JetCardResource.class);

	@Resource
	private JetCardService jetCardService;

	// ***********************
	// ANYONE
	// ***********************

	/**
	 * List all TODO query by
	 */
	@GET
	@PermitAll
	@Produces(MediaType.APPLICATION_JSON)
	public Response listAll(@QueryParam("page") @DefaultValue("0") int page,
			@QueryParam("pageSize") @DefaultValue("0") int pageSize, @Context SecurityContext context) {
		LOG.debug("List all air jet cards");
		Page<JetCard> result = jetCardService.listJetCards(page, pageSize);
		return Response.ok(result).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(result)).build();
	}

	/**
	 * Find
	 */
	@GET
	@PermitAll
	@Path("{jetCardId}")
	@Produces(MediaType.APPLICATION_JSON)
	public JetCard find(@PathParam("jetCardId") String jetCardId) {
		return jetCardService.findJetCard(jetCardId);
	}
}
