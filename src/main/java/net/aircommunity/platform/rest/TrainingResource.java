package net.aircommunity.platform.rest;

import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.TrainingBanner;
import net.aircommunity.platform.service.TrainingService;
import net.aircommunity.rest.annotation.RESTful;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

/**
 * Created by guankai on 11/04/2017.
 */
@RESTful
@Path("training")
public class TrainingResource {

    private static final Logger LOG = LoggerFactory.getLogger(TrainingResource.class);
    @Resource
    private TrainingService trainingService;

    @Path("create")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    //@RolesAllowed(Roles.ROLE_TENANT)
    @PermitAll
    public Response createTrainingBanner(@Nonnull @Valid TrainingBanner request, @Context UriInfo uriInfo) {
        LOG.debug("Created Training Banner: {}");
        TrainingBanner tb = trainingService.createTrainingBanner(request.getBannerName(), request.getBannerDesc(), request.getImageUrl(), request.getBannerUrl());
        URI uri = uriInfo.getAbsolutePathBuilder().segment(tb.getId()).build();
        LOG.debug("Created Training Banner: {}", uri);
        return Response.created(uri).build();
    }


}
