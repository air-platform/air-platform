package net.aircommunity.platform.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.aircommunity.platform.model.domain.FerryFlight;

/**
 * Repository interface for {@link FerryFlight} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface FerryFlightRepository extends BaseProductRepository<FerryFlight> {

	// Top3
	default List<FerryFlight> findTop3() {
		return findTop3ByPublishedOrderByRankAscDepartureDateDescScoreDesc(true);
	};

	List<FerryFlight> findTop3ByPublishedOrderByRankAscDepartureDateDescScoreDesc(boolean published);

	// Top3 by departure
	default List<FerryFlight> findTop3ByDeparture(String departure) {
		return findTop3ByPublishedAndDepartureOrderByRankAscDepartureDateDescScoreDesc(true, departure);
	};

	List<FerryFlight> findTop3ByPublishedAndDepartureOrderByRankAscDepartureDateDescScoreDesc(boolean published,
			String departure);

	Page<FerryFlight> findByPublishedOrderByRankAscDepartureDateDescScoreDesc(boolean published, Pageable pageable);

	/**
	 * Find FerryFlight by departure or arrival
	 * 
	 * @param location departure or arrival
	 * @param pageable the page request
	 * @return page of trans
	 */
	@Query("SELECT t FROM #{#entityName} t WHERE t.published = TRUE AND "
			+ "( t.departure LIKE CONCAT('%',:location,'%') OR "
			+ "t.arrival LIKE CONCAT('%',:location,'%') ) ORDER BY t.rank ASC, t.departureDate DESC, t.score DESC")
	Page<FerryFlight> searchByLocationForUser(@Param("location") String location, Pageable pageable);
	// Page<FerryFlight> findByDepartureContainingOrArrivalContainingOrderByRankAsc(String location, Pageable pageable);

	/**
	 * Find all by departure.
	 * 
	 * @param departure the departure
	 * @param pageable the page request
	 * @return page of FerryFlight
	 */
	@Query("SELECT t FROM #{#entityName} t WHERE t.departure = :departure AND "
			+ "t.published = TRUE ORDER BY t.rank ASC, t.departureDate DESC, t.score DESC")
	Page<FerryFlight> listByDepartureForUser(@Param("departure") String departure, Pageable pageable);
	// Page<FerryFlight> findByDepartureOrderByRankAscPriceAsc(String departure, Pageable pageable);

	/**
	 * Find all by arrival.
	 * 
	 * @param arrival the arrival
	 * @param pageable the page request
	 * @return page of FerryFlight
	 */
	@Query("SELECT t FROM #{#entityName} t WHERE t.arrival = :arrival AND "
			+ "t.published = TRUE ORDER BY t.rank ASC, t.departureDate DESC, t.score DESC")
	Page<FerryFlight> listByArrivalForUser(@Param("arrival") String arrival, Pageable pageable);
	// Page<FerryFlight> findByArrivalOrderByRankAscPriceAsc(String arrival, Pageable pageable);

}
