package net.aircommunity.platform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.domain.Airport;

/**
 * Repository interface for {@link Airport} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface AirportRepository extends JpaRepository<Airport, String> {

	List<Airport> findByNameContaining(String name);

	List<Airport> findByCityContaining(String city);

	List<Airport> findByCityContainingAndNameContaining(String city, String name);

	Airport findByIata3IgnoreCase(String iata3);

	Airport findByIcao4IgnoreCase(String icao4);
}
