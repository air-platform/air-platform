package net.aircommunity.platform.service;

import java.util.List;

import javax.annotation.Nonnull;

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
	 * List all Aprons by pagination.
	 * 
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of Aprons or empty
	 */
	@Nonnull
	Page<Apron> listAprons(int page, int pageSize);

	/**
	 * List all Aprons by pagination for a given city.
	 * 
	 * @param city the city
	 * @return a page of Aprons or empty
	 */
	@Nonnull
	List<Apron> listAprons(@Nonnull String city);

	/**
	 * List all published Aprons by pagination for a given city.
	 * 
	 * @param city the city
	 * @return a page of Aprons or empty
	 */
	@Nonnull
	List<Apron> listPublishedAprons(@Nonnull String city);

	/**
	 * Delete a Apron.
	 * 
	 * @param apronId the apronId
	 */
	void deleteApron(@Nonnull String apronId);

	/**
	 * Delete All Aprons.
	 */
	void deleteAprons();
}
