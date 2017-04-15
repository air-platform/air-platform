package net.aircommunity.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.CharterOrder;

/**
 * Repository interface for {@link CharterOrder} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface CharterOrderRepository extends BaseOrderRepository<CharterOrder> {
}
