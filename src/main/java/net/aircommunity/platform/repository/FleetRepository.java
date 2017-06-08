package net.aircommunity.platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.aircommunity.platform.model.Fleet;

/**
 * Repository interface for {@link Fleet} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface FleetRepository extends BaseProductRepository<Fleet> {

	Fleet findByFlightNo(String flightNo);

	@Query("SELECT t FROM #{#entityName} t WHERE t.published = TRUE AND t.aircraftType = :aircraftType ORDER BY t.rank ASC, t.score DESC")
	Page<Fleet> findByAircraftTypeForUser(@Param("aircraftType") String aircraftType, Pageable pageable);
	// Page<Fleet> findByAircraftTypeOrderByCreationDateDesc(String aircraftType, Pageable pageable);

	@Query("SELECT t FROM #{#entityName} t WHERE t.published = TRUE AND t.vendor.id = :provider ORDER BY t.rank ASC, t.score DESC")
	Page<Fleet> findByAircraftProviderForUser(@Param("provider") String tenantId, Pageable pageable);

	@Query("SELECT t FROM #{#entityName} t WHERE t.published = TRUE AND  t.aircraftType = :aircraftType AND t.vendor.id = :provider ORDER BY t.rank ASC, t.score DESC")
	Page<Fleet> findByAircraftTypeAndProviderForUser(@Param("aircraftType") String aircraftType,
			@Param("provider") String tenantId, Pageable pageable);

}
