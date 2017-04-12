package net.aircommunity.platform.service;

import javax.annotation.Nonnull;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.model.JetCard;
import net.aircommunity.platform.model.Page;

/**
 * JetCard service.
 * 
 * @author Bin.Zhang
 */
public interface JetCardService {

	/**
	 * Create a JetCard.
	 * 
	 * @param tenantId the tenantId
	 * @param jetCard the jetCard to be created
	 * @return JetCard created
	 */
	@Nonnull
	JetCard createJetCard(@Nonnull String tenantId, @Nonnull JetCard jetCard);

	/**
	 * Retrieves the specified JetCard.
	 * 
	 * @param jetCardId the jetCardId
	 * @return the JetCard found
	 * @throws AirException if not found
	 */
	@Nonnull
	JetCard findJetCard(@Nonnull String jetCardId);

	/**
	 * Update a JetCard.
	 * 
	 * @param jetCardId the jetCardId
	 * @param newJetCard the JetCard to be updated
	 * @return JetCard created
	 */
	@Nonnull
	JetCard updateJetCard(@Nonnull String jetCardId, @Nonnull JetCard newJetCard);

	/**
	 * List all JetCards by pagination filter by tenantId.
	 * 
	 * @param tenantId the tenantId
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of JetCards or empty
	 */
	@Nonnull
	Page<JetCard> listJetCards(@Nonnull String tenantId, int page, int pageSize);

	/**
	 * List all JetCards by pagination.
	 * 
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of JetCards or empty
	 */
	@Nonnull
	Page<JetCard> listJetCards(int page, int pageSize);

	/**
	 * Delete a JetCard.
	 * 
	 * @param jetCardId the jetCardId
	 */
	void deleteJetCard(@Nonnull String jetCardId);

	/**
	 * Delete JetCards.
	 * 
	 * @param tenantId the tenantId
	 */
	void deleteJetCards(@Nonnull String tenantId);

}
