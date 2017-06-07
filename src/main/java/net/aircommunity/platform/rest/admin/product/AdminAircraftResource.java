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
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonView;

import io.micro.annotation.RESTful;
import io.micro.common.Strings;
import net.aircommunity.platform.model.Aircraft;
import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.service.AircraftService;

/**
 * Aircraft RESTful API for ADMIN ONLY
 * 
 * @author Bin.Zhang
 */
@RESTful
@RolesAllowed({ Roles.ROLE_ADMIN })
public class AdminAircraftResource {
	private static final Logger LOG = LoggerFactory.getLogger(AdminAircraftResource.class);

	@Resource
	private AircraftService aircraftService;

	/**
	 * Create
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Admin.class)
	public Response create(@QueryParam("tenant") String tenantId, @NotNull @Valid Aircraft aircraft,
			@Context UriInfo uriInfo) {
		Aircraft created = aircraftService.createAircraft(tenantId, aircraft);
		URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
		LOG.debug("Created {}", uri);
		return Response.created(uri).build();
	}

	/**
	 * Find
	 */
	@GET
	@Path("{aircraftId}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Admin.class)
	public Aircraft find(@PathParam("aircraftId") String aircraftId) {
		return aircraftService.findAircraft(aircraftId);
	}

	/**
	 * List
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Admin.class)
	public Page<Aircraft> list(@QueryParam("tenant") String tenantId, @QueryParam("page") @DefaultValue("0") int page,
			@QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		if (Strings.isBlank(tenantId)) {
			return aircraftService.listAircrafts(page, pageSize);
		}
		return aircraftService.listAircrafts(tenantId, page, pageSize);
	}

	/**
	 * Update
	 */
	@PUT
	@Path("{aircraftId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Admin.class)
	public Aircraft update(@PathParam("aircraftId") String aircraftId, @NotNull @Valid Aircraft newAircraft) {
		return aircraftService.updateAircraft(aircraftId, newAircraft);
	}

	/**
	 * Delete
	 */
	@DELETE
	@Path("{aircraftId}")
	public void delete(@PathParam("aircraftId") String aircraftId) {
		aircraftService.deleteAircraft(aircraftId);
	}

	/**
	 * Delete all
	 */
	@DELETE
	public void deleteAll(@QueryParam("tenant") String tenantId) {
		aircraftService.deleteAircrafts(tenantId);
	}

}
