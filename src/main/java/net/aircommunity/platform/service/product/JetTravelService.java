package net.aircommunity.platform.service.product;

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
public interface JetTravelService extends StandardProductService<JetTravel> {

	/**
	 * Create a JetTravel.
	 * 
	 * @param tenantId the tenantId
	 * @param jetTravel the jetTravel to be created
	 * @return jetTravel created
	 */
	@Nonnull
	default JetTravel createJetTravel(@Nonnull String tenantId, @Nonnull JetTravel jetTravel) {
		return createProduct(tenantId, jetTravel);
	}

	/**
	 * Retrieves the specified JetTravel.
	 * 
	 * @param jetTravelId the jetTravelId
	 * @return the JetTravel found
	 * @throws AirException if not found
	 */
	@Nonnull
	default JetTravel findJetTravel(@Nonnull String jetTravelId) {
		return findProduct(jetTravelId);
	}

	/**
	 * Update a JetTravel.
	 * 
	 * @param jetTravelId the jetTravelId
	 * @param newJetTravel the JetTravel to be updated
	 * @return JetTravel created
	 */
	@Nonnull
	default JetTravel updateJetTravel(@Nonnull String jetTravelId, @Nonnull JetTravel newJetTravel) {
		return updateProduct(jetTravelId, newJetTravel);
	}

	/**
	 * List all JetTravels by pagination filter by reviewStatus. (ADMIN)
	 * 
	 * @param reviewStatus the reviewStatus
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of JetTravels or empty
	 */
	@Nonnull
	default Page<JetTravel> listAllJetTravels(@Nullable ReviewStatus reviewStatus, int page, int pageSize) {
		return listAllProducts(reviewStatus, page, pageSize);
	}

	default long countAllJetTravels(@Nullable ReviewStatus reviewStatus) {
		return countAllProducts(reviewStatus);
	}

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
	default Page<JetTravel> listTenantJetTravels(@Nonnull String tenantId, @Nullable ReviewStatus reviewStatus,
			int page, int pageSize) {
		return listTenantProducts(tenantId, reviewStatus, page, pageSize);
	}

	default long countTenantJetTravels(@Nonnull String tenantId, @Nullable ReviewStatus reviewStatus) {
		return countTenantProducts(tenantId, reviewStatus);
	}

	/**
	 * List all JetTravels by pagination. (USER)
	 * 
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of JetTravels or empty
	 */
	@Nonnull
	default Page<JetTravel> listJetTravels(int page, int pageSize) {
		return listProducts(page, pageSize);
	}

	/**
	 * List all JetTravels fuzzy match by name by pagination. (USER)
	 * 
	 * @param name the fuzzy match by name
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of JetTravels or empty
	 */
	@Nonnull
	Page<JetTravel> listJetTravelsByFuzzyName(@Nullable String name, int page, int pageSize);

	/**
	 * Delete a JetTravel.
	 * 
	 * @param jetTravelId the jetTravelId
	 */
	default void deleteJetTravel(@Nonnull String jetTravelId) {
		deleteProduct(jetTravelId);
	}

	/**
	 * Delete JetTravels.
	 * 
	 * @param tenantId the tenantId
	 */
	default void deleteJetTravels(@Nonnull String tenantId) {
		deleteProducts(tenantId);
	}

}
