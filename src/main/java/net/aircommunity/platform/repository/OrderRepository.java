package net.aircommunity.platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.domain.Order;

/**
 * Repository interface for {@link Order} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface OrderRepository extends JpaRepository<Order, String> {

	Page<Order> findByStatusNotOrderByCreationDateDesc(Pageable pageable);

	Page<Order> findByStatusOrderByCreationDateDesc(Pageable pageable);
}
