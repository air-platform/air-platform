package net.aircommunity.platform.repository;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import net.aircommunity.platform.model.AirTour;

/**
 * Created by guankai on 14/04/2017.
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
	Page<AirTour> findByCity(String city, Pageable pageable);
}
