package net.aircommunity.platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.JetCard;
import net.aircommunity.platform.model.Tenant;

/**
 * Repository interface for {@link JetCard} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface JetCardRepository extends JpaRepository<JetCard, String> {

	Page<JetCard> findByVendor(Tenant vendor, Pageable pageable);

	long deleteByVendor(Tenant vendor);

}
