package net.aircommunity.platform.service.internal;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Course;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.School;
import net.aircommunity.platform.model.Tenant;
import net.aircommunity.platform.repository.CourseRepository;
import net.aircommunity.platform.repository.SchoolRepository;
import net.aircommunity.platform.service.CourseService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Resource;

/**
 * Created by guankai on 12/04/2017.
 */
@Service
@Transactional
public class CourseServiceImpl extends AbstractServiceSupport implements CourseService {
    @Resource
    private CourseRepository courseRepository;
    @Resource
    private SchoolRepository schoolRepository;

    @Nonnull
    @Override
    public Page<Course> getCourseBySchool(@Nonnull String schoolId, int page, int pageSize) {
        return null;
    }

    @Nonnull
    @Override
    public Page<Course> getCourseByTenant(@Nonnull String tenantId, int page, int pageSize) {
        return null;
    }

    @Nonnull
    @Override
    public Page<Course> getHotCourses(int page, int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "enrollNum");
        return Pages.adapt(courseRepository.findAll(Pages.createPageRequest(page, pageSize)));
    }

    @Nonnull
    @Override
    public Course createCourse(@Nonnull Course course, @Nonnull String schoolId, @Nonnull String tenantId) {
        School school = schoolRepository.findOne(schoolId);
        if (school == null){
            throw new AirException(Codes.SCHOOL_NOT_FOUND, String.format("school %s is not found", schoolId));
        }
        Tenant tenant = findAccount(tenantId,Tenant.class);
        course.setVendor(tenant);
        course.setSchool(school);
        Course courseCreated = courseRepository.save(course);
        return courseCreated;
    }

    @Nonnull
    @Override
    public Course updateCourse(@Nonnull Course course, @Nonnull String schoolId, @Nonnull String tenantId) {
        School school = schoolRepository.findOne(schoolId);
        if (school == null){
            throw new AirException(Codes.SCHOOL_NOT_FOUND, String.format("school %s is not found", schoolId));
        }
        Tenant tenant = findAccount(tenantId,Tenant.class);
        course.setVendor(tenant);
        course.setSchool(school);
        Course courseUpdate = courseRepository.save(course);
        return courseUpdate;
    }

    @Nonnull
    @Override
    public void deleteCourse(@Nonnull String courseId) {
        Course course = courseRepository.findOne(courseId);
        if (course != null) {
            courseRepository.delete(course);
        }
    }

    @Nonnull
    @Override
    public Page<Course> getAllCourseBySchool(@Nonnull String schoolId, int page, int pageSize) {
        School school = schoolRepository.findOne(schoolId);
        if (school == null) {
            throw new AirException(Codes.SCHOOL_NOT_FOUND, String.format("school %s is not found", schoolId));
        }
        return Pages.adapt(courseRepository.findBySchool(school, Pages.createPageRequest(page, pageSize)));
    }

    @Override
    public Page<Course> getAllCourseByTenant(@Nonnull String tenantId, int page, int pageSize) {
        Tenant tenant = findAccount(tenantId, Tenant.class);
        return Pages.adapt(courseRepository.findByVendor(tenant, Pages.createPageRequest(page, pageSize)));
    }
}
