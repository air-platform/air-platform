package net.aircommunity.platform.repository;

import java.util.List;
import net.aircommunity.platform.model.domain.CitySite;
import net.aircommunity.platform.model.domain.VenueCategory;
import net.aircommunity.platform.model.domain.VenueInfo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for {@link CitySite} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 *
 * @author Xiangwen.Kong
 */
public interface VenueCategoryRepository extends JpaRepository<VenueCategory, String> {

	List<VenueCategory> findByVenueInfo(VenueInfo vi);
}
