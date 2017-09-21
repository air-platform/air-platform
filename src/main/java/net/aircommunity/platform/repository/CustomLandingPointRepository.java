package net.aircommunity.platform.repository;

import java.util.List;
import net.aircommunity.platform.model.domain.CitySite;
import net.aircommunity.platform.model.domain.CustomLandingPoint;
import net.aircommunity.platform.model.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for {@link CitySite} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 *
 * @author Xiangwen.Kong
 */
public interface CustomLandingPointRepository extends JpaRepository<CustomLandingPoint, String> {

	List<CustomLandingPoint> findByUser(User user);
}
