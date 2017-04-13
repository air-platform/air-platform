package net.aircommunity.platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.FleetCandidate;

/**
 * Repository interface for {@link FleetCandidate} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface FleetCandidateRepository extends JpaRepository<FleetCandidate, String> {

	// Page<FleetCandidate> findByFleetVendorId(String tenantId, Pageable pageable);

	Page<FleetCandidate> findByVendorId(String tenantId, Pageable pageable);

}
