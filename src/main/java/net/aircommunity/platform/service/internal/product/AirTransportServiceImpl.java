package net.aircommunity.platform.service.internal.product;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import io.micro.common.Strings;
import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.AirTransport;
import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.model.domain.Product.Category;
import net.aircommunity.platform.model.domain.ProductFamily;
import net.aircommunity.platform.model.domain.Reviewable.ReviewStatus;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.repository.AirTransportRepository;
import net.aircommunity.platform.repository.BaseProductRepository;
import net.aircommunity.platform.service.internal.Pages;
import net.aircommunity.platform.service.product.AirTransportService;
import net.aircommunity.platform.service.product.annotation.ManagedProductService;

/**
 * AirTransport service implementation.
 * 
 * @author Bin.Zhang
 */
@ManagedProductService(Product.Type.AIRTRANSPORT)
@Transactional(readOnly = true)
public class AirTransportServiceImpl extends AbstractSalesPackageProductService<AirTransport>
		implements AirTransportService {
	private static final Logger LOG = LoggerFactory.getLogger(AirTransportServiceImpl.class);

	@Resource
	private AirTransportRepository airTransportRepository;

	@Override
	protected void doCopyProperties(AirTransport src, AirTransport tgt) {
		ProductFamily family = productFamilyService.findProductFamily(src.getFamily().getId());
		if (family.getReviewStatus() != ReviewStatus.APPROVED) {
			LOG.error("ProductFamily {} is not approved yet", family);
			throw new AirException(Codes.PRODUCT_FAMILY_NOT_APPROVED, M.msg(M.PRODUCT_FAMILY_NOT_APPROVED));
		}
		tgt.setFamily(family);
		tgt.setTimeEstimation(src.getTimeEstimation());
		tgt.setFlightRoute(src.getFlightRoute());
	}

	@Override
	public List<ProductFamily> listAirTransportFamilies() {
		return productFamilyService.listProductFamilies(Category.AIR_TRANS, 1, Integer.MAX_VALUE).getContent();
	}

	@Override
	public Page<AirTransport> listAirTransportsByFamily(String familyId, int page, int pageSize) {
		if (Strings.isBlank(familyId)) {
			return listAirTransports(page, pageSize);
		}
		return Pages.adapt(airTransportRepository.findByFamilyIdAndPublishedTrueOrderByRankAscScoreDesc(familyId,
				createPageRequest(page, pageSize)));
	}

	@Override
	public Set<String> listArrivalsFromDeparture(String familyId, String departure) {
		return airTransportRepository.findArrivalsFromDeparture(familyId, departure);
	}

	@Override
	public Set<String> listDeparturesToArrival(String familyId, String arrival) {
		return airTransportRepository.findDeparturesToArrival(familyId, arrival);
	}

	@Override
	public Page<AirTransport> listAirTransportsByFuzzyLocation(String location, int page, int pageSize) {
		return Pages.adapt(airTransportRepository.findFuzzyByLocation(location, createPageRequest(page, pageSize)));
	}

	@Override
	public Page<AirTransport> listAirTransportsWithConditions(String familyId, String departure, String arrival,
			String tenantId, int page, int pageSize) {
		return Pages.adapt(airTransportRepository.findWithConditions(familyId, departure, arrival, tenantId,
				createPageRequest(page, pageSize)));
	}

	@Override
	protected Code productNotFoundCode() {
		return Codes.AIRTRANSPORT_NOT_FOUND;
	}

	@Override
	protected BaseProductRepository<AirTransport> getProductRepository() {
		return airTransportRepository;
	}

}
