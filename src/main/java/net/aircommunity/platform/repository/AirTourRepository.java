package net.aircommunity.platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import net.aircommunity.platform.model.AirTour;

/**
 * Created by guankai on 14/04/2017.
 */
public interface AirTourRepository extends BaseProductRepository<AirTour> {

	/**
	 * Find tour by city.
	 * 
	 * @param city
	 * @param pageable
	 * @return page of tour
	 */
	Page<AirTour> findByCity(String city, Pageable pageable);
}
