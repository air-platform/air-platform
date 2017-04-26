package net.aircommunity.platform.service.internal;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Course;
import net.aircommunity.platform.model.CurrencyUnit;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.School;
import net.aircommunity.platform.repository.CourseRepository;
import net.aircommunity.platform.service.CourseService;
import net.aircommunity.platform.service.SchoolService;

/**
 * Created by guankai on 12/04/2017.
 */
@Service
@Transactional
public class CourseServiceImpl extends AbstractServiceSupport implements CourseService {
	private static final String CACHE_NAME = "cache.course";

	@Resource
	private CourseRepository courseRepository;

	@Resource
	private SchoolService schoolService;

	@Override
	public Course createCourse(String schoolId, Course course) {
		School school = schoolService.findSchool(schoolId);
		Course newCourse = new Course();
		copyProperties(course, newCourse);
		newCourse.setCreationDate(new Date());
		newCourse.setEnrollNum(0);
		newCourse.setVendor(school.getVendor());
		newCourse.setSchool(school);
		return courseRepository.save(newCourse);
	}

	private void copyProperties(Course src, Course tgt) {
		tgt.setName(src.getName());
		tgt.setDescription(src.getDescription());
		tgt.setAirType(src.getAirType());
		tgt.setCourseService(src.getCourseService());
		tgt.setEnrollment(src.getEnrollment());
		tgt.setImage(src.getImage());
		tgt.setLicense(src.getLicense());
		tgt.setLocation(src.getLocation());
		tgt.setStartDate(src.getStartDate());
		tgt.setEndDate(src.getEndDate());
		tgt.setTotalNum(src.getTotalNum());
		tgt.setPrice(src.getPrice());
		tgt.setCurrencyUnit(src.getCurrencyUnit() == null ? CurrencyUnit.RMB : src.getCurrencyUnit());
		tgt.setClientManagers(src.getClientManagers());
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public Course findCourse(String courseId) {
		Course course = courseRepository.findOne(courseId);
		if (course == null) {
			throw new AirException(Codes.COURSE_NOT_FOUND, String.format("course %s not found", courseId));
		}
		return course;
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#courseId")
	@Override
	public Course updateCourse(String courseId, Course newCourse) {
		Course course = findCourse(courseId);
		School newSchool = schoolService.findSchool(newCourse.getSchool().getId());
		copyProperties(newCourse, course);
		course.setSchool(newSchool);
		return courseRepository.save(course);
	}

	@Override
	public Page<Course> listCourses(int page, int pageSize) {
		return Pages.adapt(courseRepository.findAllByOrderByStartDateDesc(Pages.createPageRequest(page, pageSize)));
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
	public Page<Course> listCoursesByAirType(String airType, int page, int pageSize) {
		Date now = new Date();
		if (airType == null) {
			return Pages.adapt(courseRepository.findByEndDateGreaterThanEqualOrderByStartDateDesc(now,
					Pages.createPageRequest(page, pageSize)));
		}
		return Pages.adapt(courseRepository.findByEndDateGreaterThanEqualAndAirTypeContainingOrderByStartDateDesc(now,
				airType, Pages.createPageRequest(page, pageSize)));
	}

	@CacheEvict(cacheNames = CACHE_NAME, key = "#courseId")
	@Override
	public void deleteCourse(String courseId) {
		courseRepository.delete(courseId);
	}

	@CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	@Override
	public void deleteCourses(String schoolId) {
		courseRepository.deleteBySchoolId(schoolId);
	}

}
