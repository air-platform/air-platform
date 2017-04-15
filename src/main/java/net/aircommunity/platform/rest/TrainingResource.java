package net.aircommunity.platform.rest;

import java.net.URI;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
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

import net.aircommunity.platform.common.net.HttpHeaders;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.TrainingBanner;
import net.aircommunity.platform.service.TrainingService;
import net.aircommunity.rest.annotation.RESTful;

/**
 * Created by guankai on 11/04/2017.
 */
@RESTful
@Path("trainings")
public class TrainingResource {

    private static final Logger LOG = LoggerFactory.getLogger(TrainingResource.class);
    @Resource
    private TrainingService trainingService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
//    @RolesAllowed(Roles.ROLE_TENANT)
    @PermitAll
    public Response createTrainingBanner(@NotNull @Valid TrainingBanner request, @Context UriInfo uriInfo) {
        TrainingBanner tb = trainingService.createTrainingBanner(request);
        URI uri = uriInfo.getAbsolutePathBuilder().segment(tb.getId()).build();
        LOG.debug("Created Training Banner: {}", uri);
        return Response.created(uri).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response getTrainingBannerList(@QueryParam("page") @DefaultValue("1") int page, @QueryParam("pageSize") @DefaultValue("10") int pageSize) {
        Page<TrainingBanner> tbPage = trainingService.getAllTrainingBanner(page, pageSize);
        return Response.ok(tbPage).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(tbPage))
                .build();
    }


}
