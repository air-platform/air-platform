package net.aircommunity.platform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.domain.Settings;

/**
 * Repository interface for {@link Settings} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface SettingsRepository extends JpaRepository<Settings, String> {

	/**
	 * Find a Settings by name
	 * 
	 * @param name the name
	 * @return an Settings or null if none
	 */
	Settings findByName(String name);

	/**
	 * Find a Settings by name starting with.
	 * 
	 * @param name the name
	 * @return an Settings or empty if none
	 */
	List<Settings> findByNameStartingWith(String name);

}
