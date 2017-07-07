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

	@Resource
	private CourseOrderService courseOrderService;

	@Override
	protected StandardOrderService<CourseOrder> getStandardOrderService() {
		return courseOrderService;
	}
}
