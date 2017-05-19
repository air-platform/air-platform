package net.aircommunity.platform.rest;

import java.net.URI;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micro.annotation.RESTful;
import io.swagger.annotations.Api;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.TrainingBanner;
import net.aircommunity.platform.service.TrainingService;

/**
 * Created by guankai on 11/04/2017.
 */
@Api
@RESTful
@Path("trainings")
@PermitAll
public class TrainingResource {
	private static final Logger LOG = LoggerFactory.getLogger(TrainingResource.class);

	@Resource
	private TrainingService trainingService;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@RolesAllowed(Roles.ROLE_ADMIN)
	public Response create(@NotNull @Valid TrainingBanner request, @Context UriInfo uriInfo) {
		TrainingBanner tb = trainingService.createTrainingBanner(request);
		URI uri = uriInfo.getAbsolutePathBuilder().segment(tb.getId()).build();
		LOG.debug("Created Training Banner: {}", uri);
		return Response.created(uri).build();
	}

	@GET
	@PermitAll
	@Produces(MediaType.APPLICATION_JSON)
	public Page<TrainingBanner> listAll(@QueryParam("page") @DefaultValue("1") int page,
			@QueryParam("pageSize") @DefaultValue("10") int pageSize) {
		return trainingService.getAllTrainingBanner(page, pageSize);
	}

}
