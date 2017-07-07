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

	@Resource
	private CourseService courseService;

	@Override
	protected StandardProductService<Course> getProductService() {
		return courseService;
	}
}