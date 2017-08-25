package net.aircommunity.platform.rest;

import com.fasterxml.jackson.annotation.JsonView;
import io.micro.annotation.RESTful;
import io.micro.common.Strings;
import io.swagger.annotations.Api;
import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.CitySite;
import net.aircommunity.platform.service.CitySiteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;

/**
 * Apron RESTful API allows list/find/query for ANYONE.
 *
 * @author Bin.Zhang
 */
@Api
@RESTful
@PermitAll
@Path("citysites")
public class CitySiteResource {
    private static final Logger LOG = LoggerFactory.getLogger(CitySiteResource.class);

    @Resource
    private CitySiteService citySiteService;

    // ***********************
    // ANYONE
    // ***********************

    /**
     * List all
     */
    @GET
    @PermitAll
    @Path("cities")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> listCities() {
        return citySiteService.listCities();
    }

    /**
     * List all
     */
    @GET
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(JsonViews.Public.class)
    public Response listAll(@QueryParam("city") String city,
                            @QueryParam("page") @DefaultValue("1") int page,
                            @QueryParam("pageSize") @DefaultValue("10") int pageSize, @Context SecurityContext context) {
        LOG.debug("List all city sites with page: {} pageSize: {}", page, pageSize);
        // city
        if (Strings.isNotBlank(city)) {
            return Response.ok(citySiteService.listCitySitesByCity(city)).build();
        }
        return Response.ok(citySiteService.listCitySites(page, pageSize)).build();
    }


    /**
     * Find
     */
    @GET
    @PermitAll
    @Path("{citySiteId}")
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(JsonViews.Public.class)
    public CitySite find(@PathParam("citySiteId") String citySiteId) {
        return citySiteService.findCitySite(citySiteId);
    }

    // *************
    // ADMIN ONLY
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
    public Response create(@NotNull @Valid CitySite CitySite, @Context UriInfo uriInfo) {
        CitySite created = citySiteService.createCitySite(CitySite);
        URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
        LOG.debug("Created {}", uri);
        return Response.created(uri).build();
    }

    /**
     * Update
     */
    @PUT
    @Path("{citySiteId}")
    @RolesAllowed(Roles.ROLE_ADMIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(JsonViews.Admin.class)
    public CitySite update(@PathParam("citySiteId") String citySiteId, @NotNull @Valid CitySite CitySite) {
        return citySiteService.updateCitySite(citySiteId, CitySite);
    }

    /**
     * Delete
     */
    @DELETE
    @Path("{citySiteId}")
    @RolesAllowed(Roles.ROLE_ADMIN)
    public void delete(@PathParam("citySiteId") String citySiteId) {
        citySiteService.deleteCitySite(citySiteId);
    }

    /**
     * Delete all
     */
    @DELETE
    @RolesAllowed(Roles.ROLE_ADMIN)
    public void deleteAll() {
        citySiteService.deleteCitySites();
    }

}
