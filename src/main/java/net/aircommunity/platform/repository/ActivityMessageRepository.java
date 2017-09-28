package net.aircommunity.platform.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import net.aircommunity.platform.model.domain.ActivityMessage;
import net.aircommunity.platform.model.domain.CitySite;
import net.aircommunity.platform.model.domain.CustomLandingPoint;
import net.aircommunity.platform.model.domain.Tenant;
import net.aircommunity.platform.model.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for {@link CitySite} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 *
 * @author Xiangwen.Kong
 */
public interface ActivityMessageRepository extends JpaRepository<ActivityMessage, String> {

	Page<ActivityMessage> findByVendor(Tenant vendor, Pageable pageable);
	Page<ActivityMessage> findByPublishedTrue(Pageable pageable);

}
