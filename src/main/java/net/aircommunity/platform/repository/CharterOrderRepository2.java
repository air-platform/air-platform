package net.aircommunity.platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.aircommunity.platform.model.CharterOrder;
import net.aircommunity.platform.model.JetCardOrder;
import net.aircommunity.platform.model.User;

/**
 * Repository interface for {@link CharterOrder} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface CharterOrderRepository2 extends JpaRepository<CharterOrder, String> {

	/**
	 * Find all user orders
	 * 
	 * @param userId the userId
	 * @param pageable page request
	 * @return a page of JetCardOrders or empty if none
	 */
	Page<CharterOrder> findByOwnerIdOrderByCreationDateDesc(String userId, Pageable pageable);

	//

	// @Query("SELECT o FROM #{#entityName} o WHERE o.owner = :owner ORDER BY o.creationDate DESC")
	// Page<CharterOrder> findBy2OwnerByOrderByCreationDateDesc(@Param("owner") User owner, Pageable pageable);

	// @Query("SELECT o FROM #{#entityName} o WHERE o.owner = :owner ORDER BY o.creationDate DESC")
	// Page<CharterOrder> findTenantOrders(@Param("owner") User owner, Pageable pageable);

	// XXX REMOVE
	// Page<CharterOrder> findByOwnerIdByOrderByCreationDateDesc(String userId, Pageable pageable);
	// Page<CharterOrder> findByOwnerByOrderByCreationDateDesc(User owner, Pageable pageable);

	long deleteByOwnerId(String ownerId);

	CharterOrder findByOrderNo(String orderNo);

	void deleteByOrderNo(String orderNo);

}
