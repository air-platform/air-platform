package net.aircommunity.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.domain.FleetCandidate;

/**
 * Repository interface for {@link FleetCandidate} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface FleetCandidateRepository extends OrderItemCandidateRepository<FleetCandidate> {

}
