package net.aircommunity.platform.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.FerryFlight;

/**
 * Repository interface for {@link FerryFlight} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface FerryFlightRepository extends BaseProductRepository<FerryFlight> {

	List<FerryFlight> findTop3ByOrderByRankAscPriceAsc();

	List<FerryFlight> findTop3ByDepartureOrderByRankAscPriceAsc(String departure);

	/**
	 * Find FerryFlight by departure or arrival
	 * 
	 * @param location departure or arrival
	 * @param pageable the page request
	 * @return page of trans
	 */
	Page<FerryFlight> findByDepartureContainingOrArrivalContainingOrderByRankAsc(String location, Pageable pageable);

	/**
	 * Find all by departure.
	 * 
	 * @param departure the departure
	 * @param pageable the page request
	 * @return page of FerryFlight
	 */
	Page<FerryFlight> findByDepartureOrderByRankAscPriceAsc(String departure, Pageable pageable);

	/**
	 * Find all by arrival.
	 * 
	 * @param arrival the arrival
	 * @param pageable the page request
	 * @return page of FerryFlight
	 */
	Page<FerryFlight> findByArrivalOrderByRankAscPriceAsc(String arrival, Pageable pageable);

}
