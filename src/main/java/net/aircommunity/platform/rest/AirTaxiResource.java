package net.aircommunity.platform.rest;

import net.aircommunity.platform.model.AirTaxi;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.service.AirTaxiService;
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
@Path("airTaxis")
public class AirTaxiResource {

    private static final Logger LOG = LoggerFactory.getLogger(CourseResource.class);

    @Resource
    private AirTaxiService airTaxiService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed(Roles.ROLE_TENANT)
    public Response create(@NotNull AirTaxi request, @Context SecurityContext context, @Context UriInfo uriInfo) {
        LOG.debug("create airTaxi start...");
        String tenantId = context.getUserPrincipal().getName();
        AirTaxi airTaxi = airTaxiService.createAirTaxi(tenantId, request);
        URI uri = uriInfo.getAbsolutePathBuilder().segment(airTaxi.getId()).build();
        LOG.debug("Created airTaxi : {}", uri);
        return Response.created(uri).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed(Roles.ROLE_TENANT)
    public Response update(@NotNull AirTaxi request, String taxiId) {
        LOG.debug("update airTaxi start...");
        airTaxiService.updateAirTaxi(taxiId, request);
        return Response.noContent().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response listAll(@QueryParam("departure") String departure, @QueryParam("page") int page, @QueryParam("pageSize") int pageSize) {
        LOG.debug("get all air tours start..");
        Page<AirTaxi> airTaxis;
        if (departure == null) {
            airTaxis = airTaxiService.listAirTaxi(page, pageSize);
        } else {
            airTaxis = airTaxiService.listAirTaxiByDeparture(departure, page, pageSize);
        }
        return Response.ok(airTaxis).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(airTaxis)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    @Path("{taxiId}")
    public Response findById(@PathParam("taxiId") String taxiId) {
        LOG.debug("get air tour by id...");
        AirTaxi airTaxi = airTaxiService.findAirTaxi(taxiId);
        return Response.ok(airTaxi).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(Roles.ROLE_TENANT)
    @Path("tenant")
    public Response listAllByTenant(@QueryParam("page") int page, @QueryParam("pageSize") int pageSize, @Context SecurityContext context) {
        LOG.debug("get air taxi by tenant");
        String tenantId = context.getUserPrincipal().getName();
        Page<AirTaxi> airTaxis = airTaxiService.listAirTaxiByTenant(tenantId, page, pageSize);
        return Response.ok(airTaxis).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(airTaxis)).build();
    }

}
