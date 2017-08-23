package net.aircommunity.platform.repository;

import net.aircommunity.platform.model.domain.Aircraft;
import net.aircommunity.platform.model.domain.PushNotification;
import net.aircommunity.platform.model.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for {@link Aircraft} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 *
 * @author Xiangwen.Kong
 */
public interface PushNotificationRepository extends JpaRepository<PushNotification, String> {

    Page<PushNotification> findByOwnerOrOwnerIsNull(User user, Pageable var1);

}
