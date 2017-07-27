package net.aircommunity.platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.Role;
import net.aircommunity.platform.model.domain.Tenant;

/**
 * Repository interface for {@link Tenant} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface TenantRepository extends JpaRepository<Tenant, String> {

	/**
	 * Find a account by role
	 * 
	 * @param role the role
	 * @return an account of page
	 */
	Page<Tenant> findByRole(Role role, Pageable pageable);
}
