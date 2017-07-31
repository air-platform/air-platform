package net.aircommunity.platform.rest.admin.product;

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
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonView;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.AirJet;
import net.aircommunity.platform.service.AirJetService;

/**
 * AirJet RESTful API allows list/find/query for ADMIN.
 * 
 * @author Bin.Zhang
 */
@RESTful
@RolesAllowed({ Roles.ROLE_ADMIN })
public class AdminAirJetResource {
	private static final Logger LOG = LoggerFactory.getLogger(AdminAirJetResource.class);

	@Resource
	private AirJetService airJetService;

	/**
	 * List all
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Admin.class)
	public Response listAll(@QueryParam("page") @DefaultValue("0") int page,
			@QueryParam("pageSize") @DefaultValue("0") int pageSize, @Context SecurityContext context) {
		LOG.debug("List all airJets with page: {} pageSize: {}", page, pageSize);
		return Response.ok(airJetService.listAirJets(page, pageSize)).build();
	}

	/**
	 * Find
	 */
	@GET
	@Path("{airJetId}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Admin.class)
	public AirJet find(@PathParam("airJetId") String airJetId) {
		return airJetService.findAirJet(airJetId);
	}

	/**
	 * Find by Type
	 */
	@GET
	@Path("type/{type}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Admin.class)
	public AirJet findByType(@PathParam("type") String type) {
		return airJetService.findAirJetByType(type);
	}

	/**
	 * Create
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Admin.class)
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
	@JsonView(JsonViews.Admin.class)
	public AirJet update(@PathParam("airJetId") String airJetId, @NotNull @Valid AirJet airJet) {
		return airJetService.updateAirJet(airJetId, airJet);
	}

	/**
	 * Delete
	 */
	@DELETE
	@Path("{airJetId}")
	public void delete(@PathParam("airJetId") String airJetId) {
		airJetService.deleteAirJet(airJetId);
	}

	/**
	 * Delete all
	 */
	@DELETE
	public void deleteAll() {
		airJetService.deleteAirJets();
	}

}
