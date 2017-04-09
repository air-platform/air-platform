package net.aircommunity.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.model.Settings;

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

}
