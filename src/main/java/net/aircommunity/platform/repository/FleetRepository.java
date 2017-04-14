package net.aircommunity.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.Fleet;

/**
 * Repository interface for {@link Fleet} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface FleetRepository extends BaseProductRepository<Fleet> {

	Fleet findByFlightNo(String flightNo);

}
