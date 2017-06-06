package net.aircommunity.platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import net.aircommunity.platform.model.AirTaxi;

/**
 * @author guankai
 */
public interface AirTaxiRepository extends BaseProductRepository<AirTaxi> {

	/**
	 * Find air taxi by departure or arrival
	 * 
	 * @param location departure or arrival
	 * @param pageable the page request
	 * @return page of taxi
	 */
	Page<AirTaxi> findByFlightRouteDepartureContainingOrFlightRouteArrivalContainingOrderByRankAsc(String location,
			Pageable pageable);

	/**
	 * Find air taxi by departure
	 * 
	 * @param departure
	 * @param pageable the page request
	 * @return page of taxi
	 */
	Page<AirTaxi> findByFlightRouteDepartureOrderByRankAsc(String departure, Pageable pageable);

	/**
	 * Find air taxi by arrival
	 * 
	 * @param arrival
	 * @param pageable the page request
	 * @return page of taxi
	 */
	Page<AirTaxi> findByFlightRouteArrivalOrderByRankAsc(String arrival, Pageable pageable);
}
