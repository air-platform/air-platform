package net.aircommunity.platform.repository;

import net.aircommunity.platform.model.Course;
import net.aircommunity.platform.model.School;
import net.aircommunity.platform.model.Tenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

/**
 * Created by guankai on 12/04/2017.
 */
public interface CourseRepository extends JpaRepository<Course, String> {
    /**
     * find courses by tenant
     *
     * @param tenant
     * @param pageable
     * @return
     */
    Page<Course> findByVendor(Tenant tenant, Pageable pageable);

    /**
     * find courses by school
     * @param school
     * @param pageable
     * @return
     */
    Page<Course> findBySchool(School school, Pageable pageable);

    /**
     * find courses by school
     *
     * @param school
     * @param pageable
     * @return
     */
    @Query("select t from Course t where t.endDate >= :now and t.school = :sc order by t.startDate desc")
    Page<Course> findBySchool2(@Param("sc") School school, @Param("now") Date now, Pageable pageable);

    @Query("select t from Course t where t.endDate >= :now and t.vendor = :te order by t.startDate desc")
    Page<Course> findByTenant2(@Param("te") Tenant tenant, @Param("now") Date now,Pageable pageable);

    /**
     * get course by airType
     * @param airType
     * @param pageable
     * @return
     */
    Page<Course> findByAirTypeContaining(String airType,Pageable pageable);

}
