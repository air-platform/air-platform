package net.aircommunity.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.FerryFlight;

/**
 * Repository interface for {@link FerryFlight} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface FerryFlightRepository extends BaseProductRepository<FerryFlight> {

}
