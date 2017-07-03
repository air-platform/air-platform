package net.aircommunity.platform.rest.user;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.CourseOrder;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.order.CourseOrderService;
import net.aircommunity.platform.service.order.StandardOrderService;

/**
 * User {@code Course} Enrollment of a {@code School} for ADMIN and USER.
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_USER })
public class UserCourseOrderResource extends UserOrderResourceSupport<CourseOrder> {
	// private static final Logger LOG = LoggerFactory.getLogger(UserCourseOrderResource.class);

	@Resource
	private CourseOrderService courseOrderService;

	@Override
	protected StandardOrderService<CourseOrder> getStandardOrderService() {
		return courseOrderService;
	}

	// /**
	// * Create
	// */
	// @POST
	// @Consumes(MediaType.APPLICATION_JSON)
	// @JsonView(JsonViews.User.class)
	// public Response create(@HeaderParam("user-agent") String userAgent, @PathParam("userId") String userId,
	// @NotNull @Valid CourseOrder order, @Context UriInfo uriInfo) {
	// LOG.debug("[{}] Creating order {}", userAgent, order);
	// CourseOrder created = courseOrderService.createCourseOrder(userId, detectOrderChannel(userAgent, order));
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
	// @JsonView(JsonViews.User.class)
	// public Page<CourseOrder> list(@PathParam("userId") String userId, @QueryParam("status") Order.Status status,
	// @QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
	// return courseOrderService.listUserCourseOrders(userId, status, page, pageSize);
	// }
}
