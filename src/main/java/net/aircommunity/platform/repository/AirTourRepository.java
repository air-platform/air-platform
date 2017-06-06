package net.aircommunity.platform.repository;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import net.aircommunity.platform.model.AirTour;

/**
 * Repository interface for {@link AirTour} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface AirTourRepository extends BaseProductRepository<AirTour> {

	/**
	 * List all tour cities.
	 * 
	 * @return a list of families
	 */
	@Query("SELECT DISTINCT t.city FROM #{#entityName} t")
	Set<String> listCities();

	/**
	 * Find tour by city.
	 * 
	 * @param city
	 * @param pageable
	 * @return page of tour
	 */
	Page<AirTour> findByCityContainingIgnoreCase(String city, Pageable pageable);
}
