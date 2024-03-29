package net.aircommunity.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.domain.FerryFlightOrder;

/**
 * Repository interface for {@link FerryFlightOrder} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface FerryFlightOrderRepository extends VendorAwareOrderRepository<FerryFlightOrder> {

}
