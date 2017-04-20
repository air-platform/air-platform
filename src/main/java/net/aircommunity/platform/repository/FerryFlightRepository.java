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

	FerryFlight findByFlightNo(String flightNo);

	List<FerryFlight> findTop3ByOrderByCreationDateDesc();

	List<FerryFlight> findTop3ByDepartureOrderByCreationDateDesc(String departure);

	/**
	 * Find all by departure.
	 * 
	 * @param pageable the page request
	 * @return page of FerryFlight
	 */
	Page<FerryFlight> findByDepartureOrderByCreationDateDesc(String departure, Pageable pageable);

}
