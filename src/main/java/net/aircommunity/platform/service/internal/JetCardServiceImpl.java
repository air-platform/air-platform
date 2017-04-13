package net.aircommunity.platform.service.internal;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.JetCard;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Tenant;
import net.aircommunity.platform.repository.JetCardRepository;
import net.aircommunity.platform.service.JetCardService;

/**
 * JetCard service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional
public class JetCardServiceImpl extends AbstractServiceSupport implements JetCardService {
	private static final String CACHE_NAME = "cache.jetcard";

	@Resource
	private JetCardRepository jetCardRepository;

	@Override
	public JetCard createJetCard(String tenantId, JetCard jetCard) {
		Tenant tenant = findAccount(tenantId, Tenant.class);
		// create new
		JetCard newCard = new JetCard();
		copyProperties(newCard, jetCard);
		// set vendor
		newCard.setVendor(tenant);
		return jetCardRepository.save(newCard);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public JetCard findJetCard(String jetCardId) {
		JetCard card = jetCardRepository.findOne(jetCardId);
		if (card == null) {
			throw new AirException(Codes.JETCARD_NOT_FOUND, String.format("JetCard %s is not found", jetCardId));
		}
		return card;
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#jetCardId")
	@Override
	public JetCard updateJetCard(String jetCardId, JetCard newJetCard) {
		JetCard card = findJetCard(jetCardId);
		copyProperties(newJetCard, card);
		return jetCardRepository.save(card);
	}

	/**
	 * Copy properties from src to tgt without ID
	 */
	private void copyProperties(JetCard src, JetCard tgt) {
		tgt.setName(src.getName());
		tgt.setType(src.getType());
		tgt.setDescription(src.getDescription());
		tgt.setCurrencyUnit(src.getCurrencyUnit());
		tgt.setSummary(src.getSummary());
		tgt.setPrice(src.getPrice());
	}

	@Override
	public Page<JetCard> listJetCards(String tenantId, int page, int pageSize) {
		Tenant tenant = findAccount(tenantId, Tenant.class);
		return Pages.adapt(jetCardRepository.findByVendor(tenant, Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<JetCard> listJetCards(int page, int pageSize) {
		return Pages.adapt(jetCardRepository.findAll(Pages.createPageRequest(page, pageSize)));
	}

	@CacheEvict(cacheNames = CACHE_NAME)
	@Override
	public void deleteJetCard(String jetCardId) {
		jetCardRepository.delete(jetCardId);
	}

	@CacheEvict(cacheNames = CACHE_NAME)
	@Override
	public void deleteJetCards(String tenantId) {
		Tenant tenant = findAccount(tenantId, Tenant.class);
		jetCardRepository.deleteByVendor(tenant);

	}

}
