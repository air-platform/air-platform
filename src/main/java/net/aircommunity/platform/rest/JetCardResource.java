package net.aircommunity.platform.rest;

import java.net.URI;

import javax.annotation.Resource;
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

import net.aircommunity.platform.common.net.HttpHeaders;
import net.aircommunity.platform.model.JetCard;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.JetCardService;
import net.aircommunity.rest.annotation.RESTful;

/**
 * Card RESTful API.
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT })
public class JetCardResource {
	private static final Logger LOG = LoggerFactory.getLogger(JetCardResource.class);

	@Resource
	private JetCardService jetCardService;

	/**
	 * Create a card
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(@PathParam("tenantId") String tenantId, @NotNull @Valid JetCard jetCard,
			@Context UriInfo uriInfo) {
		JetCard created = jetCardService.createJetCard(tenantId, jetCard);
		URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
		LOG.debug("Created {}", uri);
		return Response.created(uri).build();
	}

	/**
	 * List all cards of an tenant
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listAll(@PathParam("tenantId") String tenantId, @QueryParam("page") @DefaultValue("0") int page,
			@QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		Page<JetCard> result = jetCardService.listJetCards(tenantId, page, pageSize);
		return Response.ok(result).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(result)).build();
	}

	/**
	 * Get a card
	 */
	@GET
	@Path("{jetCardId}")
	@Produces(MediaType.APPLICATION_JSON)
	public JetCard find(@PathParam("jetCardId") String jetCardId) {
		return jetCardService.findJetCard(jetCardId);
	}

	/**
	 * Update a card
	 */
	@PUT
	@Path("{jetCardId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JetCard update(@PathParam("jetCardId") String jetCardId, @NotNull @Valid JetCard newJetCard) {
		return jetCardService.updateJetCard(jetCardId, newJetCard);
	}

	/**
	 * Delete a card
	 */
	@DELETE
	@Path("{jetCardId}")
	public Response delete(@PathParam("jetCardId") String jetCardId) {
		jetCardService.deleteJetCard(jetCardId);
		return Response.noContent().build();
	}

	/**
	 * Delete all card for a tenant
	 */
	@DELETE
	public Response deleteAll(@PathParam("tenantId") String tenantId) {
		jetCardService.deleteJetCards(tenantId);
		return Response.noContent().build();
	}

}
