package net.aircommunity.platform.repository;

import net.aircommunity.platform.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by guankai on 12/04/2017.
 */
public interface EnrollmentRepository extends JpaRepository<Enrollment, String> {
}
