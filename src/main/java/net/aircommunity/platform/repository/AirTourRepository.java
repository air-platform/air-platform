package net.aircommunity.platform.repository;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.aircommunity.platform.model.domain.AirTour;

/**
 * Repository interface for {@link AirTour} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface AirTourRepository extends BaseProductRepository<AirTour> {

	/**
	 * List all tour cities.
	 * 
	 * @return a list of families
	 */
	@Query("SELECT DISTINCT t.city FROM #{#entityName} t")
	Set<String> listCities();

	/**
	 * Find tour by city with fuzzy match.
	 * 
	 * @param city the city
	 * @param pageable
	 * @return page of tour
	 */
	@Query("SELECT t FROM #{#entityName} t WHERE t.published = TRUE AND "
			+ "t.city LIKE CONCAT('%',:city,'%')  ORDER BY t.rank DESC, t.score DESC")
	Page<AirTour> findByFuzzyCity(@Param("city") String city, Pageable pageable);

	Page<AirTour> findByPublishedTrueAndCityOrderByRankDescScoreDesc(String city, Pageable pageable);

	// Page<AirTour> findByPublishedTrueAndCityStartingWithIgnoreCase(String city, Pageable pageable);

	// just use starting with, and avoid using bellow that will perform full table scan
	// Page<AirTour> findByPublishedTrueAndCityContainingIgnoreCase(String city, Pageable pageable);
}
