package net.aircommunity.platform.service.internal;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.micro.common.Strings;
import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Course;
import net.aircommunity.platform.model.Course_;
import net.aircommunity.platform.model.CurrencyUnit;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Reviewable.ReviewStatus;
import net.aircommunity.platform.model.School;
import net.aircommunity.platform.model.Tenant;
import net.aircommunity.platform.repository.BaseProductRepository;
import net.aircommunity.platform.repository.CourseRepository;
import net.aircommunity.platform.service.CourseService;
import net.aircommunity.platform.service.SchoolService;

/**
 * CourseService Implementation.
 * 
 * @author guankai
 */
@Service
@Transactional
public class CourseServiceImpl extends AbstractProductService<Course> implements CourseService {
	private static final String CACHE_NAME = "cache.course";
	private static final String CACHE_NAME_AIRCRAFT_TYPES = "cache.course.aircraft-types";
	private static final String CACHE_NAME_AIRCRAFT_LICENSES = "cache.course.aircraft-licenses";
	private static final String CACHE_NAME_AIRCRAFT_LOCATIONS = "cache.course.aircraft-locations";

	@Resource
	private CourseRepository courseRepository;

	@Resource
	private SchoolService schoolService;

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

	@Override
	public Course createCourse(String schoolId, Course course) {
		School school = schoolService.findSchool(course.getSchool().getId());
		course.setSchool(school);
		return doCreateProduct(schoolId, course);
	}

	@Override
	protected void copyProperties(Course src, Course tgt) {
		// just set school that find previously
		tgt.setSchool(src.getSchool());
		//
		tgt.setName(src.getName());
		tgt.setImage(src.getImage());
		tgt.setClientManagers(src.getClientManagers());
		tgt.setDescription(src.getDescription());
		//
		tgt.setAircraftType(src.getAircraftType());
		tgt.setCourseService(src.getCourseService());
		tgt.setEnrollment(src.getEnrollment());
		tgt.setLicense(src.getLicense());
		tgt.setLocation(src.getLocation());
		tgt.setStartDate(src.getStartDate());
		tgt.setEndDate(src.getEndDate());
		tgt.setTotalNum(src.getTotalNum());
		tgt.setPrice(src.getPrice());
		tgt.setCurrencyUnit(src.getCurrencyUnit() == null ? CurrencyUnit.RMB : src.getCurrencyUnit());
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public Course findCourse(String courseId) {
		return doFindProduct(courseId);
		// TODO REMOVE
		// Course course = courseRepository.findOne(courseId);
		// if (course == null) {
		// throw new AirException(Codes.COURSE_NOT_FOUND, M.msg(M.COURSE_NOT_FOUND));
		// }
		// return course;
	}

	@Caching(put = @CachePut(cacheNames = CACHE_NAME, key = "#courseId"), evict = @CacheEvict(cacheNames = {
			CACHE_NAME_AIRCRAFT_TYPES, CACHE_NAME_AIRCRAFT_LICENSES }, allEntries = true))
	@Override
	public Course updateCourse(String courseId, Course newCourse) {
		Course course = findCourse(courseId);
		copyProperties(newCourse, course);
		School newSchool = schoolService.findSchool(newCourse.getSchool().getId());
		course.setSchool(newSchool);
		return safeExecute(() -> courseRepository.save(course), "Update course %s to %s failed", courseId, newCourse);
	}

	@Override
	public Page<Course> listAllCourses(ReviewStatus reviewStatus, int page, int pageSize) {
		return doListAllProducts(reviewStatus, page, pageSize);
	}

	@Override
	public long countAllCourses(ReviewStatus reviewStatus) {
		return doCountAllProducts(reviewStatus);
	}

	@Override
	public Page<Course> listTenantCourses(String tenantId, ReviewStatus reviewStatus, int page, int pageSize) {
		return doListTenantProducts(tenantId, reviewStatus, page, pageSize);
	}

	@Override
	public long countTenantCourses(String tenantId, ReviewStatus reviewStatus) {
		return doCountTenantProducts(tenantId, reviewStatus);
	}

	@Override
	public Page<Course> listCourses(int page, int pageSize) {
		return Pages.adapt(courseRepository.findAllForUser(Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public List<Course> listTop10HotCourses() {
		return courseRepository.findHotTop10(new Date());
	}

	@Override
	public Page<Course> listCoursesBySchool(String schoolId, int page, int pageSize) {
		return Pages.adapt(courseRepository.findBySchoolId(schoolId, new Date()/* now */,
				Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<Course> listCoursesByAircraftType(String aircraftType, int page, int pageSize) {
		Date now = new Date();
		if (Strings.isBlank(aircraftType)) {
			return Pages.adapt(courseRepository.findValidCourses(now, Pages.createPageRequest(page, pageSize)));
		}
		return Pages.adapt(courseRepository.findValidCoursesByAircraftType(aircraftType, now,
				Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<Course> listCoursesByLocation(String location, int page, int pageSize) {
		Date now = new Date();
		if (Strings.isBlank(location)) {
			return Pages.adapt(courseRepository.findValidCourses(now, Pages.createPageRequest(page, pageSize)));
		}
		return Pages.adapt(
				courseRepository.findValidCoursesByLocation(location, now, Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<Course> listCoursesWithConditions(String location, String license, String aircraftType, int page,
			int pageSize) {
		Specification<Course> spec = new Specification<Course>() {
			@Override
			public Predicate toPredicate(Root<Course> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				// TODO REMOVE
				// List<Predicate> predicatesList = new ArrayList<>();
				// Predicate[] p = predicatesList.toArray(new Predicate[predicatesList.size()]);
				// query.where(p).orderBy(cb.desc(root.get(Course_.startDate)));

				Predicate predicate = cb.conjunction();
				List<Expression<Boolean>> expressions = predicate.getExpressions();
				Path<Date> p = root.get(Course_.startDate);
				query.orderBy(cb.desc(p));
				if (Strings.isNotBlank(license)) {
					expressions.add(cb.equal(root.get(Course_.location), license));
				}
				if (Strings.isNotBlank(license)) {
					expressions.add(cb.equal(root.get(Course_.license), license));
				}
				if (Strings.isNotBlank(aircraftType)) {
					expressions.add(cb.equal(root.get(Course_.aircraftType), aircraftType));
				}
				expressions.add(cb.greaterThanOrEqualTo(root.get(Course_.endDate), new Date()/* now */));
				return predicate;
			}
		};
		return Pages.adapt(courseRepository.findAll(spec, Pages.createPageRequest(page, pageSize)));
	}

	@Caching(evict = { @CacheEvict(cacheNames = CACHE_NAME, key = "#courseId"),
			@CacheEvict(cacheNames = { CACHE_NAME_AIRCRAFT_TYPES, CACHE_NAME_AIRCRAFT_LICENSES }, allEntries = true) })
	@Override
	public void deleteCourse(String courseId) {
		safeExecute(() -> courseRepository.delete(courseId), "Delete course %s failed", courseId);
	}

	@CacheEvict(cacheNames = { CACHE_NAME, CACHE_NAME_AIRCRAFT_TYPES, CACHE_NAME_AIRCRAFT_LICENSES,
			CACHE_NAME_AIRCRAFT_LOCATIONS }, allEntries = true)
	@Override
	public void deleteCourses(String schoolId) {
		safeExecute(() -> courseRepository.deleteBySchoolId(schoolId), "Delete all courses for school %s", schoolId);
	}

	@Override
	protected Code productNotFoundCode() {
		return Codes.COURSE_NOT_FOUND;
	}

	@Override
	protected BaseProductRepository<Course> getProductRepository() {
		return courseRepository;
	}

	@Override
	protected Tenant doGetVendor(String schoolId) {
		School school = schoolService.findSchool(schoolId);
		return school.getVendor();
	}

}
