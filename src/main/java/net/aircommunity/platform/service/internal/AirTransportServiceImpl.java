package net.aircommunity.platform.service.internal;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.micro.common.Strings;
import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.AirTransport;
import net.aircommunity.platform.model.domain.ProductFamily;
import net.aircommunity.platform.model.domain.Product.Category;
import net.aircommunity.platform.model.domain.Reviewable.ReviewStatus;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.repository.AirTransportRepository;
import net.aircommunity.platform.repository.BaseProductRepository;
import net.aircommunity.platform.service.AirTransportService;
import net.aircommunity.platform.service.AircraftService;
import net.aircommunity.platform.service.ProductFamilyService;

/**
 * AirTransport service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional
public class AirTransportServiceImpl extends SalesPackageProductService<AirTransport> implements AirTransportService {
	private static final Logger LOG = LoggerFactory.getLogger(AirTransportServiceImpl.class);

	private static final String CACHE_NAME = "cache.airtransport";

	@Resource
	private AircraftService aircraftService;

	@Resource
	private ProductFamilyService productFamilyService;

	@Resource
	private AirTransportRepository airTransportRepository;

	@Override
	public AirTransport createAirTransport(String tenantId, AirTransport airTransport) {
		airTransport.setCategory(Category.AIR_TRANS);
		return doCreateProduct(tenantId, airTransport);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public AirTransport findAirTransport(String airTransportId) {
		return doFindProduct(airTransportId);
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#airTransportId")
	@Override
	public AirTransport updateAirTransport(String airTransportId, AirTransport newAirTransport) {
		return doUpdateProduct(airTransportId, newAirTransport);
	}

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
	public Page<AirTransport> listAllAirTransports(ReviewStatus reviewStatus, int page, int pageSize) {
		return doListAllProducts(reviewStatus, page, pageSize);
	}

	@Override
	public long countAllAirTransports(ReviewStatus reviewStatus) {
		return doCountAllProducts(reviewStatus);
	}

	@Override
	public Page<AirTransport> listTenantAirTransports(String tenantId, ReviewStatus reviewStatus, int page,
			int pageSize) {
		return doListTenantProducts(tenantId, reviewStatus, page, pageSize);
	}

	@Override
	public long countTenantAirTransports(String tenantId, ReviewStatus reviewStatus) {
		return doCountTenantProducts(tenantId, reviewStatus);
	}

	@Override
	public Page<AirTransport> listAirTransports(int page, int pageSize) {
		return doListProductsForUsers(page, pageSize);
	}

	@Override
	public Page<AirTransport> listAirTransportsByFamily(String familyId, int page, int pageSize) {
		if (Strings.isBlank(familyId)) {
			return listAirTransports(page, pageSize);
		}
		return Pages.adapt(airTransportRepository.findByFamilyId(familyId, Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<AirTransport> listAirTransportsByDeparture(String departure, int page, int pageSize) {
		return Pages.adapt(
				airTransportRepository.listByDepartureForUser(departure, Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<AirTransport> listAirTransportsByArrival(String arrival, int page, int pageSize) {
		return Pages
				.adapt(airTransportRepository.listByArrivalForUser(arrival, Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<AirTransport> searchAirTransportsByLocation(String location, int page, int pageSize) {
		return Pages.adapt(
				airTransportRepository.searchByLocationForUser(location, Pages.createPageRequest(page, pageSize)));
	}

	@CacheEvict(cacheNames = CACHE_NAME, key = "#airTransportId")
	@Override
	public void deleteAirTransport(String airTransportId) {
		doDeleteProduct(airTransportId);
	}

	@CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	@Override
	public void deleteAirTransports(String tenantId) {
		doDeleteProducts(tenantId);
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
