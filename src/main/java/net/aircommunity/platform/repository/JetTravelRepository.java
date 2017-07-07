package net.aircommunity.platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.domain.JetTravel;

/**
 * Repository interface for {@link JetTravel} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface JetTravelRepository extends BaseProductRepository<JetTravel> {

	/**
	 * Find JetTravels by name starts with.
	 * 
	 * @param name the travel name
	 * @param pageable the pageable
	 * @return page of JetTravel
	 */
	Page<JetTravel> findByPublishedTrueAndNameStartingWithIgnoreCaseOrderByRankDescScoreDesc(String name,
			Pageable pageable);

	/**
	 * Find JetTravel by name containing (Fuzzy match)
	 * 
	 * @param name the travel name
	 * @param pageable the pageable
	 * @return page of JetTravel
	 */
	// NOTE: Containing -> full table scan
	Page<JetTravel> findByPublishedTrueAndNameContainingIgnoreCaseOrderByRankDescScoreDesc(String name,
			Pageable pageable);
}
