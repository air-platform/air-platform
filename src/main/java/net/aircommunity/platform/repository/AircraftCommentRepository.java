package net.aircommunity.platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.domain.AircraftComment;

/**
 * Repository interface for {@link AircraftComment} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface AircraftCommentRepository extends JpaRepository<AircraftComment, String> {

	/**
	 * Find by comments by aircraftId.
	 * 
	 * @param aircraftId the aircraftId
	 * @param pageable the page request
	 * @return page of comment
	 */
	Page<AircraftComment> findByAircraftIdOrderByDateDesc(String aircraftId, Pageable pageable);

	long deleteByAircraftId(String aircraftId);

	long deleteByOwnerId(String accountId);

}
