package net.aircommunity.platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.aircommunity.platform.model.AirTransport;

/**
 * Repository interface for {@link AirTransport} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface AirTransportRepository extends BaseProductRepository<AirTransport> {

	/**
	 * List all transport families.
	 * 
	 * @return a list of families
	 */
	// @Query("SELECT DISTINCT t.family FROM #{#entityName} t")
	// Set<String> listFamilies();

	/**
	 * Find all AirTransport.
	 * 
	 * @param familyId the familyId
	 * @param pageable the page request
	 * @return page of products
	 */
	@Query("SELECT t FROM #{#entityName} t WHERE t.family.id = :familyId AND t.published = TRUE ORDER BY t.rank ASC, t.score DESC")
	Page<AirTransport> findByFamilyId(@Param("familyId") String familyId, Pageable pageable);
	// Page<AirTransport> findByFamilyIdAndReviewStatusAndPublishedOrderByRankAscScoreDesc(String familyId,
	// ReviewStatus reviewStatus, boolean published, Pageable pageable);
	// Page<AirTransport> findByFamilyIdOrderByRankAsc(String familyId, Pageable pageable);

	/**
	 * Find air trans by departure or arrival
	 * 
	 * @param location departure or arrival
	 * @param pageable the page request
	 * @return page of trans
	 */
	@Query("SELECT t FROM #{#entityName} t WHERE t.published = TRUE AND "
			+ "( t.flightRoute.departure LIKE CONCAT('%',:location,'%') OR "
			+ "t.flightRoute.arrival LIKE CONCAT('%',:location,'%') ) ORDER BY t.rank ASC, t.score DESC")
	Page<AirTransport> searchByLocationForUser(@Param("location") String location, Pageable pageable);
	// Page<AirTransport> findByFlightRouteDepartureContainingOrFlightRouteArrivalContainingOrderByRankAsc(String
	// location, Pageable pageable);

	/**
	 * Find air trans by departure
	 * 
	 * @param departure
	 * @param pageable the page request
	 * @return page of trans
	 */
	@Query("SELECT t FROM #{#entityName} t WHERE t.flightRoute.departure = :departure AND "
			+ "t.published = TRUE ORDER BY t.rank ASC, t.score DESC")
	Page<AirTransport> listByDepartureForUser(@Param("departure") String departure, Pageable pageable);
	// Page<AirTransport> findByFlightRouteDepartureOrderByRankAsc(String departure, Pageable pageable);

	/**
	 * Find air trans by arrival
	 * 
	 * @param arrival
	 * @param pageable the page request
	 * @return page of trans
	 */
	@Query("SELECT t FROM #{#entityName} t WHERE t.flightRoute.arrival = :arrival AND "
			+ "t.published = TRUE ORDER BY t.rank ASC, t.score DESC")
	Page<AirTransport> listByArrivalForUser(@Param("arrival") String arrival, Pageable pageable);
	// Page<AirTransport> findByFlightRouteArrivalOrderByRankAsc(String arrival, Pageable pageable);
}
