package net.aircommunity.platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.Fleet;
import net.aircommunity.platform.model.Tenant;

/**
 * Repository interface for {@link Fleet} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface FleetRepository extends JpaRepository<Fleet, String> {

	Page<Fleet> findByVendor(Tenant vendor, Pageable pageable);

	long deleteByVendor(Tenant vendor);

}
