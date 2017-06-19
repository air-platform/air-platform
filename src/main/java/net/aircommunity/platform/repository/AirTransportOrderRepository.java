package net.aircommunity.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.domain.AirTransportOrder;

/**
 * Repository interface for {@link AirTransportOrder} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface AirTransportOrderRepository extends VendorAwareOrderRepository<AirTransportOrder> {

}
