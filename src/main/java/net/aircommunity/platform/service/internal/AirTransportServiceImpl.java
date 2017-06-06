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

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.AirTransport;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Product.Category;
import net.aircommunity.platform.model.ProductFamily;
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
public class AirTransportServiceImpl extends AircraftAwareProductService<AirTransport> implements AirTransportService {
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
		if (!family.isApproved()) {
			LOG.error("ProductFamily {} is not approved yet", family);
			throw new AirException(Codes.PRODUCT_FAMILY_NOT_APPROVED, M.msg(M.PRODUCT_FAMILY_NOT_APPROVED));
		}
		tgt.setFamily(family);
		tgt.setTimeEstimation(src.getTimeEstimation());
		tgt.setFlightRoute(src.getFlightRoute());
	}

	@Override
	public List<ProductFamily> listAirTransportFamilies() {
		return productFamilyService.listProductFamiliesByCategory(Category.AIR_TRANS, 1, Integer.MAX_VALUE)
				.getContent();
	}

	@Override
	public Page<AirTransport> listAirTransports(String tenantId, int page, int pageSize) {
		return doListTenantProducts(tenantId, page, pageSize);
	}

	@Override
	public Page<AirTransport> listAirTransports(int page, int pageSize) {
		return doListAllProducts(page, pageSize);
	}

	@Override
	public Page<AirTransport> listAirTransports(boolean approved, int page, int pageSize) {
		return doListAllProducts(approved, page, pageSize);
	}

	@Override
	public long countAirTransports(boolean approved) {
		return doCountAllProducts(approved);
	}

	@Override
	public Page<AirTransport> listAirTransportsByFamily(String familyId, int page, int pageSize) {
		if (familyId == null) {
			return listAirTransports(page, pageSize);
		}
		return Pages.adapt(
				airTransportRepository.findByFamilyIdOrderByRankAsc(familyId, Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<AirTransport> listAirTransportsByDeparture(String departure, int page, int pageSize) {
		return Pages.adapt(airTransportRepository.findByFlightRouteDepartureOrderByRankAsc(departure,
				Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<AirTransport> listAirTransportsByArrival(String arrival, int page, int pageSize) {
		return Pages.adapt(airTransportRepository.findByFlightRouteArrivalOrderByRankAsc(arrival,
				Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<AirTransport> searchAirTransportsByLocation(String location, int page, int pageSize) {
		return Pages.adapt(
				airTransportRepository.findByFlightRouteDepartureContainingOrFlightRouteArrivalContainingOrderByRankAsc(
						location, Pages.createPageRequest(page, pageSize)));
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
