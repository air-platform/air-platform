package net.aircommunity.platform.repository;

import java.util.Date;

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
	 * Find all Users
	 * 
	 * @return an account of page
	 */
	Page<User> findAllByOrderByCreationDateDesc(Pageable pageable);

	/**
	 * Find a account by role
	 * 
	 * @param role the role
	 * @return an account of page
	 */
	Page<User> findByRoleOrderByCreationDateDesc(Role role, Pageable pageable);

	/**
	 * Count new users for current month. (e.g. 2017-07-01 00:00:00 - 2017-07-01 23:59:59)
	 * 
	 * @return count of new users
	 */
	default long countThisMonth() {
		return CountSupport.countThisMonth(this::countByCreationDateBetween);
	}

	/**
	 * Count new users for today. (e.g. 2017-07-11 00:00:00 - 2017-07-11 23:59:59)
	 * 
	 * @return count of new users
	 */
	default long countToday() {
		return CountSupport.countToday(this::countByCreationDateBetween);
	}

	long countByCreationDateBetween(Date firstDate, Date lastDate);

}
