package net.aircommunity.platform.rest.admin.product;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.Course;
import net.aircommunity.platform.service.product.CourseService;
import net.aircommunity.platform.service.product.StandardProductService;

/**
 * Course RESTful API for ADMIN
 * 
 * @author Bin.Zhang
 */
@RESTful
@RolesAllowed(Roles.ROLE_ADMIN)
public class AdminCourseResource extends AdminProductResourceSupport<Course> {
	// private static final Logger LOG = LoggerFactory.getLogger(AdminCourseResource.class);

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
	// @JsonView(JsonViews.Admin.class)
	// public Response create(@NotNull @Valid Course request, @Context UriInfo uriInfo) {
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
	// @JsonView(JsonViews.Admin.class)
	// public Page<Course> list(@QueryParam("tenant") String tenantId, @QueryParam("status") ReviewStatus reviewStatus,
	// @QueryParam("page") @DefaultValue("1") int page, @QueryParam("pageSize") @DefaultValue("10") int pageSize) {
	// if (Strings.isBlank(tenantId)) {
	// return courseService.listAllCourses(reviewStatus, page, pageSize);
	// }
	// return courseService.listTenantCourses(tenantId, reviewStatus, page, pageSize);
	// }
	//
	// /**
	// * Count to be reviewed
	// */
	// @GET
	// @Path("review/count")
	// @Produces(MediaType.APPLICATION_JSON)
	// public JsonObject listToBeApproved(@QueryParam("tenant") String tenantId,
	// @QueryParam("status") ReviewStatus reviewStatus) {
	// if (Strings.isBlank(tenantId)) {
	// return buildCountResponse(courseService.countAllCourses(reviewStatus));
	// }
	// return buildCountResponse(courseService.countTenantCourses(tenantId, reviewStatus));
	// }
	//
	// /**
	// * Update
	// */
	// @PUT
	// @Path("{courseId}")
	// @Consumes(MediaType.APPLICATION_JSON)
	// @JsonView(JsonViews.Admin.class)
	// public Course update(@PathParam("courseId") String courseId, @NotNull @Valid Course request) {
	// return courseService.updateCourse(courseId, request);
	// }
}