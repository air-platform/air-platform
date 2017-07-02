package net.aircommunity.platform.service;

import javax.annotation.Nonnull;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.AirClass;

/**
 * AirClass service (ADMIN ONLY)
 * 
 * @author Bin.Zhang
 */
public interface AirClassService {

	/**
	 * Create a AirClass.
	 * 
	 * @param airClass the air class to be created
	 * @return air class created
	 */
	@Nonnull
	AirClass createAirClass(@Nonnull AirClass airClass);

	/**
	 * Retrieves the specified AirClass.
	 * 
	 * @param airClassId the airClassId
	 * @return the AirClass found
	 * @throws AirException if not found
	 */
	@Nonnull
	AirClass findAirClass(@Nonnull String airClassId);

	/**
	 * Update a AirClass.
	 * 
	 * @param airClassId the airClassId
	 * @param newAirClass the AirClass to be updated
	 * @return AirClass created
	 */
	@Nonnull
	AirClass updateAirClass(@Nonnull String airClassId, @Nonnull AirClass newAirClass);

	/**
	 * Increase a AirClass views.
	 * 
	 * @param airClassId the airClassId
	 * @return AirClass created
	 */
	@Nonnull
	AirClass increaseAirClassViews(@Nonnull String airClassId);

	/**
	 * List all AirClasses by pagination.
	 * 
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of AirClasss or empty
	 */
	@Nonnull
	Page<AirClass> listAirClasses(int page, int pageSize);

	/**
	 * Delete a AirClass.
	 * 
	 * @param airClassId the airClassId
	 */
	void deleteAirClass(@Nonnull String airClassId);

	/**
	 * Delete All AirClasses.
	 */
	void deleteAirClasses();

}
