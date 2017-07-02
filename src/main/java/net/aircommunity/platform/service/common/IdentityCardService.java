package net.aircommunity.platform.service.common;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.aircommunity.platform.model.IdCardInfo;

/**
 * Resident identification card service.
 * 
 * @author Bin.Zhang
 */
public interface IdentityCardService {

	/**
	 * Verify if the ID card number matches the person name.
	 * 
	 * @param cardNo the ID card number
	 * @param name the person name of ID card
	 * @return true if matches, false otherwise
	 */
	boolean verifyIdentityCard(@Nonnull String cardNo, @Nonnull String name);

	/**
	 * Get ID card info.
	 * 
	 * @param cardNo the ID card number
	 * @param name the person name of ID card
	 * @return ID Card Info or null if the card No. and name is invalid
	 */
	@Nullable
	IdCardInfo getIdCardInfo(@Nonnull String cardNo, @Nonnull String name);

}
