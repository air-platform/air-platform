package net.aircommunity.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.JetTravelOrder;

/**
 * Repository interface for {@link JetTravelOrder} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface JetTravelOrderRepository extends VendorAwareOrderRepository<JetTravelOrder> {
}
