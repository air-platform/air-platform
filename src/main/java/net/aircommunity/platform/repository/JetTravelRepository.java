package net.aircommunity.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.JetTravel;

/**
 * Repository interface for {@link JetTravel} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface JetTravelRepository extends BaseProductRepository<JetTravel> {

}
