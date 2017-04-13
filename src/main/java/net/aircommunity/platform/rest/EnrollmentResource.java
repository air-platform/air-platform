package net.aircommunity.platform.rest;

import net.aircommunity.platform.common.net.HttpHeaders;
import net.aircommunity.platform.model.Enrollment;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.service.EnrollmentService;
import net.aircommunity.rest.annotation.RESTful;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;

/**
 * Created by guankai on 13/04/2017.
 */
@RESTful
@Path("enrollments")
public class EnrollmentResource {

    private static final Logger LOG = LoggerFactory.getLogger(CourseResource.class);
    @Resource
    private EnrollmentService enrollmentService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response getAllEnrollments(@QueryParam("page") @DefaultValue("1") int page, @QueryParam("pageSize") @DefaultValue("10") int pageSize) {
        LOG.debug("get all enrollments...");
        Page<Enrollment> enrollmentPage = enrollmentService.getAllEnrollment(page, pageSize);
        return Response.ok(enrollmentPage).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(enrollmentPage))
                .build();
    }

    @GET
    @Path("user")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response getUserEnrollments(@QueryParam("page") @DefaultValue("1") int page, @QueryParam("pageSize") @DefaultValue("10") int pageSize, @Context SecurityContext context) {
        LOG.debug("get user all enrollments");
        String userId = context.getUserPrincipal().getName();
        Page<Enrollment> enrollmentPage = enrollmentService.getEnrollmentByUser(userId, page, pageSize);
        return Response.ok(enrollmentPage).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(enrollmentPage))
                .build();
    }

    @GET
    @Path("course/{courseId}")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response getCourseEnrollments(@QueryParam("page") @DefaultValue("1") int page, @QueryParam("pageSize") @DefaultValue("10") int pageSize, @PathParam("courseId") String courseId) {
        LOG.debug("get school all enrollments");
        Page<Enrollment> enrollmentPage = enrollmentService.getEnrollmentByCourse(courseId, page, pageSize);
        return Response.ok(enrollmentPage).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(enrollmentPage))
                .build();
    }

    @GET
    @Path("tenant")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response getTenantEnrollments(@QueryParam("page") @DefaultValue("1") int page, @QueryParam("pageSize") @DefaultValue("10") int pageSize, @Context SecurityContext context) {
        LOG.debug("get tenant all enrollments");
        String tenantId = context.getUserPrincipal().getName();
        Page<Enrollment> enrollmentPage = enrollmentService.getEnrollmentByTenant(tenantId, page, pageSize);
        return Response.ok(enrollmentPage).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(enrollmentPage))
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response createEnrollment(@NotNull Enrollment request, @PathParam("courseId") String courseId, @Context SecurityContext context, @Context UriInfo uriInfo) {
        LOG.debug("create enrollment");
        String userId = context.getUserPrincipal().getName();
        Enrollment enrollment = enrollmentService.createEnrollment(request, courseId, userId);
        URI uri = uriInfo.getAbsolutePathBuilder().segment(enrollment.getId()).build();
        LOG.debug("Created enrollment : {}", uri);
        return Response.created(uri).build();
    }

    @POST
    @Path("cancel/{enrollmentId}")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response cancelEnrollment(@PathParam("enrollmentId") String enrollmentId) {
        LOG.debug("cancel enrollment...");
        enrollmentService.cancelEnrollment(enrollmentId);
        return Response.noContent().build();
    }


}
