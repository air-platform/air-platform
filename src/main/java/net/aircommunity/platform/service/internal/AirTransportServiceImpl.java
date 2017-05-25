package net.aircommunity.platform.service.internal;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.AirTransport;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Product.Category;
import net.aircommunity.platform.model.ProductFamily;
import net.aircommunity.platform.repository.AirTransportRepository;
import net.aircommunity.platform.repository.BaseProductRepository;
import net.aircommunity.platform.repository.ProductFamilyRepository;
import net.aircommunity.platform.service.AirTransportService;
import net.aircommunity.platform.service.AircraftService;

/**
 * AirTransport service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional
public class AirTransportServiceImpl extends AircraftAwareService<AirTransport> implements AirTransportService {
	private static final String CACHE_NAME = "cache.airtransport";

	@Resource
	private AircraftService aircraftService;

	@Resource
	private AirTransportRepository airTransportRepository;

	@Resource
	private ProductFamilyRepository productFamilyRepository;

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
		tgt.setFamily(src.getFamily());
		tgt.setTimeEstimation(src.getTimeEstimation());
		tgt.setFlightRoute(src.getFlightRoute());
	}

	@Override
	public List<ProductFamily> listAirTransportFamilies() {
		return productFamilyRepository.findByCategory(Category.AIR_TRANS);
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
	public Page<AirTransport> listAirTransportsByFamily(String familyId, int page, int pageSize) {
		if (familyId == null) {
			return listAirTransports(page, pageSize);
		}
		return Pages.adapt(airTransportRepository.findByFamilyIdOrderByCreationDateDesc(familyId,
				Pages.createPageRequest(page, pageSize)));
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
