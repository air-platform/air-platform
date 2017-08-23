package net.aircommunity.platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.Role;
import net.aircommunity.platform.model.domain.Account;

/**
 * Repository interface for {@link Account} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface AccountRepository extends JpaRepository<Account, String> {

	/**
	 * Find a account by apiKey
	 * 
	 * @param apiKey the apiKey
	 * @return an account or null if none
	 */
	Account findByApiKey(String apiKey);

	/**
	 * Find a account by role
	 * 
	 * @return an account of page
	 */
	Page<Account> findAllByOrderByCreationDateDesc(Pageable pageable);

	/**
	 * Find a account by role
	 * 
	 * @param role the role
	 * @return an account of page
	 */
	Page<Account> findByRoleOrderByCreationDateDesc(Role role, Pageable pageable);

	/**
	 * Count by role.
	 * 
	 * @param role the role
	 * @return the count of accounts
	 */
	long countByRole(Role role);

}
