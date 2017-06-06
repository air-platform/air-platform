package net.aircommunity.platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

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
	// Page<AirTransport> findByFamilyIdOrderByCreationDateDesc(String familyId, Pageable pageable);
	Page<AirTransport> findByFamilyIdOrderByRankAsc(String familyId, Pageable pageable);

	/**
	 * Find air trans by departure or arrival
	 * 
	 * @param location departure or arrival
	 * @param pageable the page request
	 * @return page of trans
	 */
	Page<AirTransport> findByFlightRouteDepartureContainingOrFlightRouteArrivalContainingOrderByRankAsc(String location,
			Pageable pageable);

	/**
	 * Find air trans by departure
	 * 
	 * @param departure
	 * @param pageable the page request
	 * @return page of trans
	 */
	Page<AirTransport> findByFlightRouteDepartureOrderByRankAsc(String departure, Pageable pageable);

	/**
	 * Find air trans by arrival
	 * 
	 * @param arrival
	 * @param pageable the page request
	 * @return page of trans
	 */
	Page<AirTransport> findByFlightRouteArrivalOrderByRankAsc(String arrival, Pageable pageable);
}
