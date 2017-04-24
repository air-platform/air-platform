package net.aircommunity.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.AircraftItem;

/**
 * Repository interface for {@link AircraftItem} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface AircraftItemRepository extends JpaRepository<AircraftItem, String> {

}
