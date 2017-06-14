package net.aircommunity.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.AirClass;

/**
 * Repository interface for {@link AirClass} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface AirClassRepository extends JpaRepository<AirClass, String> {

}
