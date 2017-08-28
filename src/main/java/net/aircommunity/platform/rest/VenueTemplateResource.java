package net.aircommunity.platform.rest;

import com.fasterxml.jackson.annotation.JsonView;
import io.micro.annotation.RESTful;
import io.swagger.annotations.Api;
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
import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.VenueTemplate;
import net.aircommunity.platform.service.VenueTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Venue template RESTful API allows list/find/query for ANYONE.
 *
 * @author Xiangwen.Kong
 */
@Api
@RESTful
@PermitAll
@Path("venueTemplates")
public class VenueTemplateResource {
	private static final Logger LOG = LoggerFactory.getLogger(VenueTemplateResource.class);

	@Resource
	private VenueTemplateService venueTemplateService;

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
		return Response.ok(venueTemplateService.listVenueTemplates(page, pageSize)).build();
	}


	/**
	 * Find
	 */
	@GET
	@PermitAll
	@Path("{venueTemplateId}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Public.class)
	public VenueTemplate find(@PathParam("venueTemplateId") String venueTemplateId) {
		return venueTemplateService.findVenueTemplate(venueTemplateId);
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
	@RolesAllowed(Roles.ROLE_ADMIN)
	@Consumes(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Admin.class)
	public Response create(@NotNull @Valid VenueTemplate VenueTemplate, @Context UriInfo uriInfo) {
		VenueTemplate created = venueTemplateService.createVenueTemplate(VenueTemplate);
		URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
		LOG.debug("Created {}", uri);
		return Response.created(uri).build();
	}

	/**
	 * Update
	 */
	@PUT
	@Path("{venueTemplateId}")
	@RolesAllowed(Roles.ROLE_ADMIN)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Admin.class)
	public VenueTemplate update(@PathParam("venueTemplateId") String venueTemplateId, @NotNull @Valid VenueTemplate VenueTemplate) {
		return venueTemplateService.updateVenueTemplate(venueTemplateId, VenueTemplate);
	}

	/**
	 * Delete
	 */
	@DELETE
	@Path("{venueTemplateService}")
	@RolesAllowed(Roles.ROLE_ADMIN)
	public void delete(@PathParam("venueTemplateId") String venueTemplateId) {
		venueTemplateService.deleteVenueTemplate(venueTemplateId);
	}

	/**
	 * Delete all
	 */
	@DELETE
	@RolesAllowed(Roles.ROLE_ADMIN)
	public void deleteAll() {
		venueTemplateService.deleteVenueTemplates();
	}

}
