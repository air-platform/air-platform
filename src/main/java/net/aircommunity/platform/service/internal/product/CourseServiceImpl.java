package net.aircommunity.platform.service.internal.product;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.transaction.annotation.Transactional;

import io.micro.common.Strings;
import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.Course;
import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.model.domain.School;
import net.aircommunity.platform.model.domain.Tenant;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.repository.BaseProductRepository;
import net.aircommunity.platform.repository.CourseRepository;
import net.aircommunity.platform.service.internal.Pages;
import net.aircommunity.platform.service.product.CourseService;
import net.aircommunity.platform.service.product.annotation.ManagedProductService;

/**
 * Course service implementation.
 * 
 * @author Bin.Zhang
 */
@ManagedProductService(Product.Type.COURSE)
@Transactional(readOnly = true)
public class CourseServiceImpl extends AbstractStandardProductService<Course> implements CourseService {
	private static final String CACHE_NAME_AIRCRAFT_TYPES = "cache.course.aircraft-types";
	private static final String CACHE_NAME_AIRCRAFT_LICENSES = "cache.course.aircraft-licenses";
	private static final String CACHE_NAME_AIRCRAFT_LOCATIONS = "cache.course.aircraft-locations";

	@Resource
	private CourseRepository courseRepository;

	@Cacheable(cacheNames = CACHE_NAME_AIRCRAFT_TYPES)
	@Override
	public Set<String> listAircraftTypes() {
		return courseRepository.listAircraftTypes();
	}

	@Cacheable(cacheNames = CACHE_NAME_AIRCRAFT_LICENSES)
	@Override
	public Set<String> listAircraftLicenses() {
		return courseRepository.listAircraftLicenses();
	}

	@Cacheable(cacheNames = CACHE_NAME_AIRCRAFT_LOCATIONS)
	@Override
	public Set<String> listCourseLocations() {
		return courseRepository.listCourseLocations();
	}

	@Transactional
	@Override
	public Course createCourse(String schoolId, Course course) {
		School school = schoolService.findSchool(course.getSchool().getId());
		course.setSchool(school);
		return doCreateProduct(schoolId, course);
	}

	@Transactional
	@Caching(put = @CachePut(cacheNames = CACHE_NAME, key = "#courseId"), evict = @CacheEvict(cacheNames = {
			CACHE_NAME_AIRCRAFT_TYPES, CACHE_NAME_AIRCRAFT_LICENSES }, allEntries = true))
	@Override
	public Course updateCourse(String courseId, Course newCourse) {
		School newSchool = schoolService.findSchool(newCourse.getSchool().getId());
		newCourse.setSchool(newSchool);
		return doUpdateProduct(courseId, newCourse);
	}

	@Override
	protected void copyProperties(Course src, Course tgt) {
		// just set school that find previously
		tgt.setSchool(src.getSchool());
		tgt.setAircraftType(src.getAircraftType());
		tgt.setCourseService(src.getCourseService());
		tgt.setEnrollment(src.getEnrollment());
		tgt.setLicense(src.getLicense());
		tgt.setLocation(src.getLocation());
		tgt.setStartDate(src.getStartDate());
		tgt.setEndDate(src.getEndDate());
		tgt.setTotalNum(src.getTotalNum());
	}

	// never called from REST
	@Override
	public Page<Course> listCourses(int page, int pageSize) {
		return Pages.adapt(courseRepository
				.findByPublishedTrueOrderByRankDescStartDateDescScoreDesc(createPageRequest(page, pageSize)));
	}

	@Override
	public List<Course> listTop10HotCourses() {
		return courseRepository.findHotTop10(new Date());
	}

	@Override
	public Page<Course> listCoursesBySchool(String schoolId, int page, int pageSize) {
		return Pages.adapt(courseRepository.findBySchoolId(schoolId, createPageRequest(page, pageSize)));
	}

	@Override
	public Page<Course> listCoursesByLocation(String location, int page, int pageSize) {
		if (Strings.isBlank(location)) {
			return Pages.adapt(courseRepository.findAllCourses(createPageRequest(page, pageSize)));
		}
		return Pages.adapt(courseRepository.findByFuzzyLocation(location, createPageRequest(page, pageSize)));
	}

	@Override
	public Page<Course> listCoursesWithConditions(String location, String aircraftType, String license, int page,
			int pageSize) {
		return Pages.adapt(
				courseRepository.findByConditions(location, aircraftType, license, createPageRequest(page, pageSize)));
	}

	@Transactional
	@Caching(evict = { @CacheEvict(cacheNames = CACHE_NAME, key = "#courseId"),
			@CacheEvict(cacheNames = { CACHE_NAME_AIRCRAFT_TYPES, CACHE_NAME_AIRCRAFT_LICENSES }, allEntries = true) })
	@Override
	public void deleteCourse(String courseId) {
		doDeleteProduct(courseId);
	}

	@Transactional
	@CacheEvict(cacheNames = { CACHE_NAME, CACHE_NAME_AIRCRAFT_TYPES, CACHE_NAME_AIRCRAFT_LICENSES,
			CACHE_NAME_AIRCRAFT_LOCATIONS }, allEntries = true)
	@Override
	public void deleteCourses(String schoolId) {
		doBatchDeleteProducts(() -> courseRepository.findBySchoolId(schoolId), Codes.PRODUCT_CANNOT_BE_DELETED,
				M.msg(M.SCHOOL_COURSES_CANNOT_BE_DELETED, schoolId));
	}

	@Override
	protected Tenant doGetVendor(String schoolId) {
		School school = schoolService.findSchool(schoolId);
		return school.getVendor();
	}

	@Override
	protected Code productNotFoundCode() {
		return Codes.COURSE_NOT_FOUND;
	}

	@Override
	protected BaseProductRepository<Course> getProductRepository() {
		return courseRepository;
	}

	// TODO REMOVE
	// @Override
	// public Page<Course> listCoursesByAircraftType(String aircraftType, int page, int pageSize) {
	// Date now = new Date();
	// if (Strings.isBlank(aircraftType)) {
	// return Pages.adapt(courseRepository.findValidCourses(now, createPageRequest(page, pageSize)));
	// }
	// return Pages.adapt(courseRepository.findValidCoursesByAircraftType(aircraftType, now,
	// createPageRequest(page, pageSize)));
	// }

}
