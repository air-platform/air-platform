package net.aircommunity.platform.service.internal;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Product.Category;
import net.aircommunity.platform.model.Promotion;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.repository.PromotionRepository;
import net.aircommunity.platform.service.PromotionService;

/**
 * Product Promotion Service implementation. (for platform AMDIN only)
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional
public class PromotionServiceImpl extends AbstractServiceSupport implements PromotionService {
	private static final String CACHE_NAME = "cache.promotion";

	@Resource
	private PromotionRepository promotionRepository;

	@Override
	public Promotion createPromotion(Promotion promotion) {
		Promotion newPromotion = new Promotion();
		newPromotion.setCreationDate(new Date());
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

	@CachePut(cacheNames = CACHE_NAME, key = "#promotionId")
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

	@Override
	public List<Promotion> listPromotions(Category category) {
		if (category == null) {
			return promotionRepository.findAll();
		}
		return promotionRepository.findByCategory(category);
	}

	@CacheEvict(cacheNames = CACHE_NAME, key = "#promotionId")
	@Override
	public void deletePromotion(String promotionId) {
		safeExecute(() -> promotionRepository.delete(promotionId), "Delete promotion %s failed", promotionId);
	}

	@CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	@Override
	public void deletePromotions() {
		safeExecute(() -> promotionRepository.deleteAll(), "Delete all promotions failed");
	}

}
