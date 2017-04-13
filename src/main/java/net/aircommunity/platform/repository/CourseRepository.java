package net.aircommunity.platform.repository;

import net.aircommunity.platform.model.Course;
import net.aircommunity.platform.model.School;
import net.aircommunity.platform.model.Tenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

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
     *
     * @param school
     * @param pageable
     * @return
     */
    Page<Course> findBySchool(School school, Pageable pageable);

}
