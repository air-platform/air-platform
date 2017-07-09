package net.aircommunity.platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.domain.Fleet;

/**
 * Repository interface for {@link Fleet} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface FleetRepository extends BaseProductRepository<Fleet> {

	/**
	 * Find by flightNo
	 * 
	 * @param flightNo
	 * @return fleet found or null
	 */
	Fleet findByFlightNo(String flightNo);

	// NOTE: both query and findBy WORKS, using findBy for type safe

	/**
	 * Find all published fleets by aircraftType. (USER)
	 * 
	 * @param aircraftType the aircraftType
	 * @param pageable the pageable
	 * @return a page of fleets or empty
	 */
	Page<Fleet> findByPublishedTrueAndAircraftTypeOrderByRankDescScoreDesc(String aircraftType, Pageable pageable);
	// @Query("SELECT t FROM #{#entityName} t WHERE t.published = TRUE AND t.aircraftType = :aircraftType ORDER BY
	// t.rank DESC, t.score DESC")
	// Page<Fleet> findByAircraftTypeForUser(@Param("aircraftType") String aircraftType, Pageable pageable);

	/**
	 * Find all published fleets by tenantId. (USER)
	 * 
	 * @param tenantId the tenantId
	 * @param pageable the pageable
	 * @return a page of fleets or empty
	 */
	Page<Fleet> findByPublishedTrueAndVendorIdOrderByRankDescScoreDesc(String tenantId, Pageable pageable);
	// @Query("SELECT t FROM #{#entityName} t WHERE t.published = TRUE AND t.vendor.id = :provider ORDER BY t.rank DESC,
	// t.score DESC")
	// Page<Fleet> findByAircraftProviderForUser(@Param("provider") String tenantId, Pageable pageable);

	/**
	 * Find all published fleets by tenantId and aircraftType. (USER)
	 * 
	 * @param tenantId the tenantId
	 * @param aircraftType the aircraftType
	 * @param pageable the pageable
	 * @return a page of fleets or empty
	 */
	Page<Fleet> findByPublishedTrueAndVendorIdAndAircraftTypeOrderByRankDescScoreDesc(String tenantId,
			String aircraftType, Pageable pageable);
	// @Query("SELECT t FROM #{#entityName} t WHERE t.published = TRUE AND t.aircraftType = :aircraftType AND
	// t.vendor.id = :provider ORDER BY t.rank DESC, t.score DESC")
	// Page<Fleet> findByAircraftTypeAndProviderForUser(@Param("aircraftType") String aircraftType,
	// @Param("provider") String tenantId, Pageable pageable);

}
