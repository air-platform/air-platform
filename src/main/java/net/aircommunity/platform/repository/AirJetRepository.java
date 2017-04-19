package net.aircommunity.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.AirJet;

/**
 * Repository interface for {@link AirJet} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface AirJetRepository extends JpaRepository<AirJet, String> {

	AirJet findByType(String type);
}
