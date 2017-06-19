package net.aircommunity.platform.service;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.model.domain.Promotion;
import net.aircommunity.platform.model.domain.Product.Category;

/**
 * Product Promotion Service.
 * 
 * @author Bin.Zhang
 */
public interface PromotionService {

	/**
	 * Create a promotion.
	 * 
	 * @param promotion the promotion to be created
	 * @return promotion created
	 */
	@Nonnull
	Promotion createPromotion(@Nonnull Promotion promotion);

	/**
	 * Retrieves the specified Promotion.
	 * 
	 * @param promotionId the promotionId
	 * @return the Promotion found
	 * @throws AirException if not found
	 */
	@Nonnull
	Promotion findPromotion(@Nonnull String promotionId);

	/**
	 * Update a Promotion.
	 * 
	 * @param promotionId the promotionId
	 * @param newPromotion the Promotion to be updated
	 * @return Promotion created
	 */
	@Nonnull
	Promotion updatePromotion(@Nonnull String promotionId, @Nonnull Promotion newPromotion);

	/**
	 * List all Promotions
	 * 
	 * @param category the promotion category or null to list all
	 * @return a list of Promotions or empty
	 */
	@Nonnull
	List<Promotion> listPromotions(@Nullable Category category);

	/**
	 * Delete a Promotion.
	 * 
	 * @param promotionId the promotionId
	 */
	void deletePromotion(@Nonnull String promotionId);

	/**
	 * Delete All Promotions.
	 */
	void deletePromotions();
}
