package net.aircommunity.platform.rest.tenant;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.Course;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.product.CourseService;
import net.aircommunity.platform.service.product.StandardProductService;

/**
 * Course RESTful API for ADMIN/TENANT
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT, Roles.ROLE_CUSTOMER_SERVICE })
public class TenantCourseResource extends TenantProductResourceSupport<Course> {
	// private static final Logger LOG = LoggerFactory.getLogger(TenantCourseResource.class);

	@Resource
	private CourseService courseService;

	@Override
	protected StandardProductService<Course> getProductService() {
		return courseService;
	}

	// /**
	// * Create
	// */
	// @POST
	// @Consumes(MediaType.APPLICATION_JSON)
	// @JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	// public Response create(@NotNull @Valid Course request, @Context UriInfo uriInfo) {
	// LOG.debug("Creating: {}", request);
	// Course created = courseService.createCourse(request.getSchool().getId(), request);
	// URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
	// LOG.debug("Created: {}", uri);
	// return Response.created(uri).build();
	// }
	//
	// /**
	// * List
	// */
	// @GET
	// @Produces(MediaType.APPLICATION_JSON)
	// @JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	// public Page<Course> list(@PathParam("tenantId") String tenantId, @QueryParam("status") ReviewStatus reviewStatus,
	// @QueryParam("page") @DefaultValue("1") int page, @QueryParam("pageSize") @DefaultValue("10") int pageSize) {
	// return courseService.listTenantCourses(tenantId, reviewStatus, page, pageSize);
	// }
	//
	// /**
	// * Count to be reviewed
	// */
	// @GET
	// @Path("review/count")
	// @Produces(MediaType.APPLICATION_JSON)
	// public JsonObject listToBeApproved(@PathParam("tenantId") String tenantId,
	// @QueryParam("status") ReviewStatus reviewStatus) {
	// return buildCountResponse(courseService.countTenantCourses(tenantId, reviewStatus));
	// }
	//
	// /**
	// * Update
	// */
	// @PUT
	// @Path("{courseId}")
	// @Consumes(MediaType.APPLICATION_JSON)
	// @JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	// public Course update(@PathParam("courseId") String courseId, @NotNull @Valid Course request) {
	// return courseService.updateCourse(courseId, request);
	// }

}