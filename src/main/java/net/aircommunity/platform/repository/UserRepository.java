package net.aircommunity.platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.Role;
import net.aircommunity.platform.model.domain.User;

/**
 * Repository interface for {@link User} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface UserRepository extends JpaRepository<User, String> {

	/**
	 * Find a account by apiKey
	 * 
	 * @param role the role
	 * @return an account of page
	 */
	Page<User> findByRole(Role role, Pageable pageable);

}
