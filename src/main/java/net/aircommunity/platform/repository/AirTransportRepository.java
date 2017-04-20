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
	 * Find all AirTransport.
	 * 
	 * @param family the family
	 * @param pageable the page request
	 * @return page of products
	 */
	Page<AirTransport> findByFamilyOrderByCreationDateDesc(String family, Pageable pageable);
}
