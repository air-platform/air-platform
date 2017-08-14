package net.aircommunity.platform.service;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.Apron;
import net.aircommunity.platform.model.domain.CitySite;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * CitySite service (ADMIN ONLY)
 * 
 * @author Xiangwen.Kong
 */
public interface CitySiteService {

	/**
	 * Create a apron.
	 * 
	 * @param citySite the citySite to be created
	 * @return citySite created
	 */
	@Nonnull
	CitySite createCitySite(@Nonnull CitySite citySite);

	/**
	 * Retrieves the specified Apron.
	 * 
	 * @param citySiteId the citySiteId
	 * @return the CitySite found
	 * @throws AirException if not found
	 */
	@Nonnull
	CitySite findCitySite(@Nonnull String citySiteId);

	/**
	 * Update a Apron.
	 * 
	 * @param citySiteId the citySiteId
	 * @param newCitySite the CitySite to be updated
	 * @return CitySite created
	 */
	@Nonnull
	CitySite updateCitySite(@Nonnull String citySiteId, @Nonnull CitySite newCitySite);

	/**
	 * List all aprons by pagination.
	 * 
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of CitySites or empty
	 */
	@Nonnull
	Page<CitySite> listCitySites(int page, int pageSize);

	/**
	 * List all published aprons by pagination for a given city.
	 * 
	 * @param city the city
	 * @return a page of aprons or empty
	 */
	@Nonnull
	List<CitySite> listCitySitesByCity(@Nonnull String city);
	List<String> listCities();

	/**
	 * Delete a CitySite.
	 * 
	 * @param citySiteId the citySiteId
	 */
	void deleteCitySite(@Nonnull String citySiteId);

	/**
	 * Delete All CitySites.
	 */
	void deleteCitySites();
}
