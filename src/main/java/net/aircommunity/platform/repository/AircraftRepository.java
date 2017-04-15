package net.aircommunity.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.Aircraft;

/**
 * Repository interface for {@link Aircraft} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface AircraftRepository extends BaseProductRepository<Aircraft> {

	Aircraft findByFlightNo(String flightNo);
}
