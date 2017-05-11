package net.aircommunity.platform.service;

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
	boolean verifyIdentityCard(String cardNo, String name);

}
