package net.aircommunity.platform.rest;

import net.aircommunity.platform.model.AirTour;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.service.AirTourService;
import net.aircommunity.rest.annotation.RESTful;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.aircommunity.platform.common.net.HttpHeaders;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;

/**
 * Created by guankai on 15/04/2017.
 */
@RESTful
@Path("airTours")
public class AirTourResourse {

    private static final Logger LOG = LoggerFactory.getLogger(CourseResource.class);
    @Resource
    private AirTourService airTourService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed(Roles.ROLE_TENANT)
    public Response createAirTour(@NotNull AirTour request, @Context SecurityContext context, @Context UriInfo uriInfo) {
        LOG.debug("create airTour start...");
        String tenantId = context.getUserPrincipal().getName();
        AirTour airTour = airTourService.createTour(request, tenantId);
        URI uri = uriInfo.getAbsolutePathBuilder().segment(airTour.getId()).build();
        LOG.debug("Created airTour : {}", uri);
        return Response.created(uri).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed(Roles.ROLE_TENANT)
    @Path("{tourId}")
    public Response updateAirTour(@NotNull String tourId, @NotNull AirTour request) {
        LOG.debug("update airTour start....");
        airTourService.updateTour(tourId, request);
        return Response.noContent().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response getAllAirTours(@QueryParam("city") String city, @QueryParam("page") int page, @QueryParam("pageSize") int pageSize) {
        LOG.debug("get all air tours start..");
        Page<AirTour> airTours;
        if (city == null) {
            airTours = airTourService.getAllTours(page, pageSize);
        } else {
            airTours = airTourService.getToursByCity(city, page, pageSize);
        }
        return Response.ok(airTours).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(airTours)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    @Path("{tourId}")
    public Response getAirTourById(@PathParam("tourId") String tourId) {
        LOG.debug("get air tour by id...");
        AirTour airTour = airTourService.getTourById(tourId);
        return Response.ok(airTour).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(Roles.ROLE_TENANT)
    @Path("tenant")
    public Response getAirTourByTenant(@QueryParam("page") int page, @QueryParam("pageSize") int pageSize, @Context SecurityContext context) {
        LOG.debug("get air tour by tenant");
        String tenantId = context.getUserPrincipal().getName();
        Page<AirTour> airTours = airTourService.getToursByTenant(tenantId, page, pageSize);
        return Response.ok(airTours).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(airTours)).build();
    }
}
