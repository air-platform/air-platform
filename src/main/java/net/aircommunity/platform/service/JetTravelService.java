package net.aircommunity.platform.service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.JetTravel;
import net.aircommunity.platform.model.domain.Reviewable.ReviewStatus;

/**
 * JetTravel service.
 * 
 * @author Bin.Zhang
 */
public interface JetTravelService {

	/**
	 * Create a JetTravel.
	 * 
	 * @param tenantId the tenantId
	 * @param JetTravel the JetTravel to be created
	 * @return JetTravel created
	 */
	@Nonnull
	JetTravel createJetTravel(@Nonnull String tenantId, @Nonnull JetTravel jetTravel);

	/**
	 * Retrieves the specified JetTravel.
	 * 
	 * @param jetTravelId the jetTravelId
	 * @return the JetTravel found
	 * @throws AirException if not found
	 */
	@Nonnull
	JetTravel findJetTravel(@Nonnull String jetTravelId);

	/**
	 * Update a JetTravel.
	 * 
	 * @param jetTravelId the jetTravelId
	 * @param newJetTravel the JetTravel to be updated
	 * @return JetTravel created
	 */
	@Nonnull
	JetTravel updateJetTravel(@Nonnull String jetTravelId, @Nonnull JetTravel newJetTravel);

	/**
	 * List all JetTravels by pagination filter by reviewStatus. (ADMIN)
	 * 
	 * @param reviewStatus the reviewStatus
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of JetTravels or empty
	 */
	@Nonnull
	Page<JetTravel> listAllJetTravels(@Nullable ReviewStatus reviewStatus, int page, int pageSize);

	long countAllJetTravels(@Nullable ReviewStatus reviewStatus);

	/**
	 * List tenant JetTravels by pagination filter by reviewStatus. (TENANT)
	 * 
	 * @param tenantId the tenantId
	 * @param reviewStatus the reviewStatus
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of JetTravels or empty
	 */
	@Nonnull
	Page<JetTravel> listTenantJetTravels(@Nonnull String tenantId, @Nullable ReviewStatus reviewStatus, int page,
			int pageSize);

	long countTenantJetTravels(@Nonnull String tenantId, @Nullable ReviewStatus reviewStatus);

	/**
	 * List all JetTravels by pagination. (USER)
	 * 
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of JetTravels or empty
	 */
	@Nonnull
	Page<JetTravel> listJetTravels(int page, int pageSize);

	/**
	 * List all JetTravels fuzzy match by name by pagination. (USER)
	 * 
	 * @param name the fuzzy match by name
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of JetTravels or empty
	 */
	@Nonnull
	Page<JetTravel> searchJetTravels(@Nullable String name, int page, int pageSize);

	/**
	 * Delete a JetTravel.
	 * 
	 * @param jetTravelId the jetTravelId
	 */
	void deleteJetTravel(@Nonnull String jetTravelId);

	/**
	 * Delete JetTravels.
	 * 
	 * @param tenantId the tenantId
	 */
	void deleteJetTravels(@Nonnull String tenantId);

}
