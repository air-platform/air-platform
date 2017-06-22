package net.aircommunity.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.domain.AirTourOrder;

/**
 * Repository interface for {@link AirTourOrder} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author guankai
 */
public interface AirTourOrderRepository extends VendorAwareOrderRepository<AirTourOrder> {

}
