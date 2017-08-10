package net.aircommunity.platform.service;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.Apron;

/**
 * Apron service (ADMIN ONLY)
 * 
 * @author Bin.Zhang
 */
public interface ApronService {

	/**
	 * Create a apron.
	 * 
	 * @param apron the apron to be created
	 * @return apron created
	 */
	@Nonnull
	Apron createApron(@Nonnull Apron apron);

	/**
	 * Retrieves the specified Apron.
	 * 
	 * @param apronId the apronId
	 * @return the Apron found
	 * @throws AirException if not found
	 */
	@Nonnull
	Apron findApron(@Nonnull String apronId);

	/**
	 * Update a Apron.
	 * 
	 * @param apronId the apronId
	 * @param newApron the Apron to be updated
	 * @return Apron created
	 */
	@Nonnull
	Apron updateApron(@Nonnull String apronId, @Nonnull Apron newApron);

	/**
	 * Publish Apron or not.
	 * 
	 * @param apronId the apronId
	 * @param published make it visible for users if true, false otherwise
	 * @return Apron update
	 */
	@Nonnull
	Apron publishApron(@Nonnull String apronId, boolean published);

	/**
	 * List all aprons by pagination.
	 * 
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of aprons or empty
	 */
	@Nonnull
	Page<Apron> listAprons(int page, int pageSize);

	/**
	 * List all published aprons by pagination for a given city.
	 * 
	 * @param city the city
	 * @return a page of aprons or empty
	 */
	@Nonnull
	List<Apron> listPublishedCityAprons(@Nonnull String city, @Nullable Apron.Type type);

	List<Apron> listPublishedProvinceAprons(@Nonnull String province, @Nullable Apron.Type type);

	List<Apron> listNearPublishedAprons(@Nonnull String province, @Nonnull int distance, @Nonnull double latitude, @Nonnull double longitude, @Nullable Apron.Type type);

	List<String> listCities();
	
	List<String> listProvinces();

	/**
	 * Delete a Apron.
	 * 
	 * @param apronId the apronId
	 */
	void deleteApron(@Nonnull String apronId);

	/**
	 * Delete All aprons.
	 */
	void deleteAprons();
}
