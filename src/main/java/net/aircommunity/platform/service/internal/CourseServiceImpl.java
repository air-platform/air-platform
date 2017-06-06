package net.aircommunity.platform.service.internal;

import java.util.Date;
import java.util.List;

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

	@Resource
	private CourseRepository courseRepository;

	@Resource
	private SchoolService schoolService;

	@Override
	protected Tenant doGetVendor(String schoolId) {
		School school = schoolService.findSchool(schoolId);
		return school.getVendor();
	}

	@Override
	public Course createCourse(String schoolId, Course course) {
		return doCreateProduct(schoolId, course);
	}

	protected void copyProperties(Course src, Course tgt) {
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

	@CachePut(cacheNames = CACHE_NAME, key = "#courseId")
	@Override
	public Course updateCourse(String courseId, Course newCourse) {
		Course course = findCourse(courseId);
		School newSchool = schoolService.findSchool(newCourse.getSchool().getId());
		copyProperties(newCourse, course);
		course.setSchool(newSchool);
		return safeExecute(() -> courseRepository.save(course), "Update course %s to %s failed", courseId, newCourse);
	}

	@Override
	public Page<Course> listCourses(int page, int pageSize) {
		return Pages.adapt(courseRepository.findAllByOrderByStartDateDesc(Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<Course> listCourses(boolean approved, int page, int pageSize) {
		return doListAllProducts(approved, page, pageSize);
	}

	@Override
	public long countCourses(boolean approved) {
		return doCountAllProducts(approved);
	}

	@Override
	public Page<Course> listCourses(String tenantId, int page, int pageSize) {
		return Pages.adapt(
				courseRepository.findByVendorIdOrderByStartDateDesc(tenantId, Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public List<Course> listTop10HotCourses() {
		return courseRepository.findTop10ByEndDateGreaterThanEqualOrderByEnrollNumDesc(new Date());
	}

	@Override
	public Page<Course> listCoursesBySchool(String schoolId, int page, int pageSize) {
		return Pages.adapt(courseRepository.findBySchoolIdAndEndDateGreaterThanEqualOrderByStartDateDesc(schoolId,
				new Date()/* now */, Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<Course> listCoursesByAircraftType(String aircraftType, int page, int pageSize) {
		Date now = new Date();
		if (Strings.isBlank(aircraftType)) {
			return Pages.adapt(courseRepository.findByEndDateGreaterThanEqualOrderByStartDateDesc(now,
					Pages.createPageRequest(page, pageSize)));
		}
		return Pages.adapt(courseRepository.findByEndDateGreaterThanEqualAndAircraftTypeContainingOrderByStartDateDesc(
				now, aircraftType, Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<Course> listCoursesByLocation(String location, int page, int pageSize) {
		Date now = new Date();
		if (Strings.isBlank(location)) {
			return Pages.adapt(courseRepository.findByEndDateGreaterThanEqualOrderByStartDateDesc(now,
					Pages.createPageRequest(page, pageSize)));
		}
		return Pages.adapt(courseRepository.findByEndDateGreaterThanEqualAndLocationContainingOrderByStartDateDesc(now,
				location, Pages.createPageRequest(page, pageSize)));
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

	@CacheEvict(cacheNames = CACHE_NAME, key = "#courseId")
	@Override
	public void deleteCourse(String courseId) {
		safeExecute(() -> courseRepository.delete(courseId), "Delete course %s failed", courseId);
	}

	@CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
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
}
