package net.aircommunity.platform.service;

import java.util.List;

import javax.annotation.Nonnull;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.model.AirJet;
import net.aircommunity.platform.model.Page;

/**
 * AirJet service (only available for platform ADMIN).
 * 
 * @author Bin.Zhang
 */
public interface AirJetService {

	/**
	 * Create a airJet.
	 * 
	 * @param airJet the airJet to be created
	 * @return airJet created
	 */
	@Nonnull
	AirJet createAirJet(@Nonnull AirJet airJet);

	/**
	 * Retrieves the specified AirJet.
	 * 
	 * @param airJetId the airJetId
	 * @return the AirJet found
	 * @throws AirException if not found
	 */
	@Nonnull
	AirJet findAirJet(@Nonnull String airJetId);

	/**
	 * Retrieves the specified AirJet.
	 * 
	 * @param type the air jet type
	 * @return the AirJet found
	 * @throws AirException if not found
	 */
	@Nonnull
	AirJet findAirJetByType(@Nonnull String type);

	/**
	 * Update a AirJet.
	 * 
	 * @param airJetId the airJetId
	 * @param newAirJet the AirJet to be updated
	 * @return AirJet created
	 */
	@Nonnull
	AirJet updateAirJet(@Nonnull String airJetId, @Nonnull AirJet newAirJet);

	/**
	 * List all AirJets by pagination.
	 * 
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of AirJets or empty
	 */
	@Nonnull
	Page<AirJet> listAirJets(int page, int pageSize);

	/**
	 * List all AirJets
	 * 
	 * @return a list of AirJets or empty
	 */
	@Nonnull
	default List<AirJet> listAirJets() {
		return listAirJets(1, Integer.MAX_VALUE).getContent();
	}

	/**
	 * Delete a AirJet.
	 * 
	 * @param airJetId the airJetId
	 */
	void deleteAirJet(@Nonnull String airJetId);

	/**
	 * Delete All AirJets.
	 */
	void deleteAirJets();

}
