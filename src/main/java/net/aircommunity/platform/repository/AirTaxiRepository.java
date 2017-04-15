package net.aircommunity.platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import net.aircommunity.platform.model.AirTaxi;

/**
 * Created by guankai on 14/04/2017.
 */
public interface AirTaxiRepository extends BaseProductRepository<AirTaxi> {

	/**
	 * Find air taxi by departure
	 * 
	 * @param departure
	 * @param pageable
	 * @return page of taxi
	 */
	Page<AirTaxi> findByDeparture(String departure, Pageable pageable);
}
