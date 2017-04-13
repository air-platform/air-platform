package net.aircommunity.platform.repository;

import net.aircommunity.platform.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Created by guankai on 12/04/2017.
 */
public interface EnrollmentRepository extends JpaRepository<Enrollment, String> {

    Page<Enrollment> findByOwnerId(String userId, Pageable pageable);

    Page<Enrollment> findByCourse(Course course, Pageable pageable);

    @Query("select t from Enrollment t where t.course.vendor = :te")
    Page<Enrollment> findByTenant(@Param("te") Tenant tenant, Pageable pageable);

    @Query("select t from Enrollment t where t.course.school = :sc")
    Page<Enrollment> findBySchool(@Param("sc") School school, Pageable pageable);


}
