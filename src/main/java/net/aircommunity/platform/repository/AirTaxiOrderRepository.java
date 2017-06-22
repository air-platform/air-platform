package net.aircommunity.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.domain.AirTaxiOrder;

/**
 * Repository interface for {@link AirTaxiOrder} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author guankai
 */
public interface AirTaxiOrderRepository extends VendorAwareOrderRepository<AirTaxiOrder> {

}
