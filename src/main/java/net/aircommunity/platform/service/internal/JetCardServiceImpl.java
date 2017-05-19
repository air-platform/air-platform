package net.aircommunity.platform.service.internal;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.JetCard;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.repository.BaseProductRepository;
import net.aircommunity.platform.repository.JetCardRepository;
import net.aircommunity.platform.service.JetCardService;

/**
 * JetCard service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional
public class JetCardServiceImpl extends AbstractProductService<JetCard> implements JetCardService {
	private static final String CACHE_NAME = "cache.jetcard";

	@Resource
	private JetCardRepository jetCardRepository;

	@Override
	public JetCard createJetCard(String tenantId, JetCard jetCard) {
		return doCreateProduct(tenantId, jetCard);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public JetCard findJetCard(String jetCardId) {
		return doFindProduct(jetCardId);
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#jetCardId")
	@Override
	public JetCard updateJetCard(String jetCardId, JetCard newJetCard) {
		return doUpdateProduct(jetCardId, newJetCard);
	}

	/**
	 * Copy properties from src to tgt without ID
	 */
	@Override
	protected void copyProperties(JetCard src, JetCard tgt) {
		tgt.setType(src.getType());
		tgt.setCurrencyUnit(src.getCurrencyUnit());
		tgt.setSummary(src.getSummary());
		tgt.setPrice(src.getPrice());
	}

	@Override
	public Page<JetCard> listJetCards(String tenantId, int page, int pageSize) {
		return doListTenantProducts(tenantId, page, pageSize);
	}

	@Override
	public Page<JetCard> listJetCards(int page, int pageSize) {
		return doListAllProducts(page, pageSize);
	}

	@CacheEvict(cacheNames = CACHE_NAME, key = "#jetCardId")
	@Override
	public void deleteJetCard(String jetCardId) {
		doDeleteProduct(jetCardId);
	}

	@CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	@Override
	public void deleteJetCards(String tenantId) {
		doDeleteProducts(tenantId);
	}

	@Override
	protected Code productNotFoundCode() {
		return Codes.JETCARD_NOT_FOUND;
	}

	@Override
	protected BaseProductRepository<JetCard> getProductRepository() {
		return jetCardRepository;
	}

}
