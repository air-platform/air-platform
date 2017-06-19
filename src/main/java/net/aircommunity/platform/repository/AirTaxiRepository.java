package net.aircommunity.platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.aircommunity.platform.model.domain.AirTaxi;

/**
 * Repository interface for {@link AirTaxi} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface AirTaxiRepository extends BaseProductRepository<AirTaxi> {

	/**
	 * Find air taxi by departure or arrival
	 * 
	 * @param location departure or arrival
	 * @param pageable the page request
	 * @return page of taxi
	 */
	@Query("SELECT t FROM #{#entityName} t WHERE t.published = TRUE AND "
			+ "( t.flightRoute.departure LIKE CONCAT('%',:location,'%') OR "
			+ "t.flightRoute.arrival LIKE CONCAT('%',:location,'%') ) ORDER BY t.rank ASC, t.score DESC")
	Page<AirTaxi> searchByLocationForUser(@Param("location") String location, Pageable pageable);
	// Page<AirTaxi> findByFlightRouteDepartureContainingOrFlightRouteArrivalContainingOrderByRankAscScoreDesc(
	// String location, Pageable pageable);

	/**
	 * Find air taxi by departure
	 * 
	 * @param departure
	 * @param pageable the page request
	 * @return page of taxi
	 */
	@Query("SELECT t FROM #{#entityName} t WHERE t.flightRoute.departure = :departure AND "
			+ "t.published = TRUE ORDER BY t.rank ASC, t.score DESC")
	Page<AirTaxi> listAirTaxisByDepartureForUser(@Param("departure") String departure, Pageable pageable);
	// Page<AirTaxi> findByFlightRouteDepartureOrderByRankAscScoreDesc(String departure, Pageable pageable);

	/**
	 * Find air taxi by arrival
	 * 
	 * @param arrival
	 * @param pageable the page request
	 * @return page of taxi
	 */
	@Query("SELECT t FROM #{#entityName} t WHERE t.flightRoute.arrival = :arrival AND "
			+ "t.published = TRUE ORDER BY t.rank ASC, t.score DESC")
	Page<AirTaxi> listAirTaxisByArrivalForUser(@Param("arrival") String arrival, Pageable pageable);
	// Page<AirTaxi> findByFlightRouteArrivalOrderByRankAscScoreDesc(String arrival, Pageable pageable);
}
