package net.aircommunity.platform.service.internal;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.domain.Product.Category;
import net.aircommunity.platform.model.domain.Promotion;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.repository.PromotionRepository;
import net.aircommunity.platform.service.PromotionService;

/**
 * Product Promotion Service implementation. (for platform AMDIN only)
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional(readOnly = true)
public class PromotionServiceImpl extends AbstractServiceSupport implements PromotionService {
	private static final String CACHE_NAME = "cache.promotion";
	private static final String CACHE_NAME_CATEGORY = "cache.promotion-category";

	@Resource
	private PromotionRepository promotionRepository;

	@Transactional
	@CacheEvict(cacheNames = CACHE_NAME_CATEGORY, allEntries = true)
	@Override
	public Promotion createPromotion(Promotion promotion) {
		Promotion newPromotion = new Promotion();
		newPromotion.setCreationDate(new Date());
		newPromotion.setRank(Promotion.DEFAULT_RANK);
		copyProperties(promotion, newPromotion);
		return safeExecute(() -> promotionRepository.save(newPromotion), "Create promotion %s failed", promotion);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public Promotion findPromotion(String promotionId) {
		Promotion promotion = promotionRepository.findOne(promotionId);
		if (promotion == null) {
			throw new AirException(Codes.PRODUCT_PROMOTION_NOT_FOUND, M.msg(M.PROMOTION_NOT_FOUND));
		}
		return promotion;
	}

	@Transactional
	@Caching(put = @CachePut(cacheNames = CACHE_NAME, key = "#promotionId"), evict = @CacheEvict(cacheNames = CACHE_NAME_CATEGORY, allEntries = true))
	@Override
	public Promotion updatePromotion(String promotionId, Promotion newPromotion) {
		Promotion promotion = findPromotion(promotionId);
		copyProperties(newPromotion, promotion);
		return safeExecute(() -> promotionRepository.save(promotion), "Update promotion %s to %s failed", promotionId,
				newPromotion);
	}

	private void copyProperties(Promotion src, Promotion tgt) {
		tgt.setCategory(src.getCategory());
		tgt.setName(src.getName());
		tgt.setDescription(src.getDescription());
		tgt.setItems(src.getItems());
	}

	@Transactional
	@Caching(put = @CachePut(cacheNames = CACHE_NAME, key = "#promotionId"), evict = @CacheEvict(cacheNames = CACHE_NAME_CATEGORY, allEntries = true))
	public Promotion updatePromotionRank(String promotionId, int rank) {
		Promotion promotion = findPromotion(promotionId);
		if (rank < 0) {
			rank = Promotion.DEFAULT_RANK;
		}
		promotion.setRank(rank);
		return safeExecute(() -> promotionRepository.save(promotion), "Update promotion %s rank to %d failed",
				promotionId, rank);
	}

	@Cacheable(cacheNames = CACHE_NAME_CATEGORY)
	@Override
	public List<Promotion> listPromotions(Category category) {
		if (category == null) {
			return promotionRepository.findAllByOrderByRankDescCreationDateDesc();
		}
		return promotionRepository.findByCategoryOrderByRankDescCreationDateDesc(category);
	}

	@Transactional
	@Caching(evict = { @CacheEvict(cacheNames = CACHE_NAME, key = "#promotionId"),
			@CacheEvict(cacheNames = CACHE_NAME_CATEGORY, allEntries = true) })
	@Override
	public void deletePromotion(String promotionId) {
		safeExecute(() -> promotionRepository.delete(promotionId), "Delete promotion %s failed", promotionId);
	}

	@Transactional
	@CacheEvict(cacheNames = { CACHE_NAME, CACHE_NAME_CATEGORY }, allEntries = true)
	@Override
	public void deletePromotions() {
		safeExecute(() -> promotionRepository.deleteAll(), "Delete all promotions failed");
	}

}
