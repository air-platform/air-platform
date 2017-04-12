package net.aircommunity.platform.rest;

import net.aircommunity.platform.common.net.HttpHeaders;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.School;
import net.aircommunity.platform.service.SchoolService;
import net.aircommunity.rest.annotation.RESTful;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;

/**
 * Created by guankai on 12/04/2017.
 */
@RESTful
@Path("schools")
public class SchoolResource {

    private static final Logger LOG = LoggerFactory.getLogger(SchoolResource.class);

    @Resource
    private SchoolService schoolService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSchoolList(@QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("10") int pageSize) {
        Page<School> schoolPage = schoolService.getSchoolList(page, pageSize);
        return Response.ok(schoolPage).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(schoolPage))
                .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createSchool(@NotNull School request, @Context SecurityContext context, @Context UriInfo uriInfo){
        LOG.debug("create school start...");
        String accountId = context.getUserPrincipal().getName(); // 获取商户信息
        School schoolCreated = schoolService.createSchool(request,accountId);
        URI uri = uriInfo.getAbsolutePathBuilder().segment(schoolCreated.getId()).build();
        LOG.debug("Created school : {}", uri);
        return Response.created(uri).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateSchool(@NotNull School request,@Context SecurityContext context){
        LOG.debug("update school start...");
        String accountId = context.getUserPrincipal().getName();
        schoolService.updateSchool(request,accountId);
        return Response.noContent().build();
    }


}