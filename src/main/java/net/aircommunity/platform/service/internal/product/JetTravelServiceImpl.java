package net.aircommunity.platform.service.internal.product;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.micro.common.Strings;
import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.JetTravel;
import net.aircommunity.platform.repository.BaseProductRepository;
import net.aircommunity.platform.repository.JetTravelRepository;
import net.aircommunity.platform.service.internal.Pages;
import net.aircommunity.platform.service.product.JetTravelService;

/**
 * JetTravel service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional(readOnly = true)
public class JetTravelServiceImpl extends AbstractStandardProductService<JetTravel> implements JetTravelService {

	@Resource
	private JetTravelRepository jetTravelRepository;

	@Override
	public Page<JetTravel> listJetTravelsByFuzzyName(String name, int page, int pageSize) {
		if (Strings.isBlank(name)) {
			return listJetTravels(page, pageSize);
		}
		// NOTE: the first 1 has better performance, but less match result
		// return
		// Pages.adapt(jetTravelRepository.findByPublishedTrueAndNameStartingWithIgnoreCaseOrderByRankDescScoreDesc(
		// name, createPageRequest(page, pageSize)));
		return Pages.adapt(jetTravelRepository.findByPublishedTrueAndNameContainingIgnoreCaseOrderByRankDescScoreDesc(
				name, createPageRequest(page, pageSize)));
	}

	@Override
	protected Code productNotFoundCode() {
		return Codes.JETTRAVEL_NOT_FOUND;
	}

	@Override
	protected BaseProductRepository<JetTravel> getProductRepository() {
		return jetTravelRepository;
	}
}
