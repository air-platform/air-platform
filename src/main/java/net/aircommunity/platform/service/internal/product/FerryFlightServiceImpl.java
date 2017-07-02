package net.aircommunity.platform.service.internal.product;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.micro.common.Strings;
import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.FerryFlight;
import net.aircommunity.platform.repository.BaseProductRepository;
import net.aircommunity.platform.repository.FerryFlightRepository;
import net.aircommunity.platform.service.internal.Pages;
import net.aircommunity.platform.service.product.FerryFlightService;

/**
 * FerryFlight service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional(readOnly = true)
public class FerryFlightServiceImpl extends AbstractStandardProductService<FerryFlight> implements FerryFlightService {

	// TODO REMOVE
	// private static final String CACHE_NAME = "cache.ferryflight";

	@Resource
	private FerryFlightRepository ferryFlightRepository;

	// XXX
	// @Override
	// public FerryFlight createFerryFlight(String tenantId, FerryFlight ferryFlight) {
	// return doCreateProduct(tenantId, ferryFlight);
	// }
	//
	// @Cacheable(cacheNames = CACHE_NAME)
	// @Override
	// public FerryFlight findFerryFlight(String ferryFlightId) {
	// return doFindProduct(ferryFlightId);
	// }
	//
	// @CachePut(cacheNames = CACHE_NAME, key = "#ferryFlightId")
	// @Override
	// public FerryFlight updateFerryFlight(String ferryFlightId, FerryFlight newFerryFlight) {
	// return doUpdateProduct(ferryFlightId, newFerryFlight);
	// }

	@Override
	protected void copyProperties(FerryFlight src, FerryFlight tgt) {
		tgt.setFlightNo(src.getFlightNo());
		tgt.setAircraftType(src.getAircraftType());
		tgt.setArrival(src.getArrival());
		tgt.setTimeSlot(src.getTimeSlot());
		tgt.setDepartureDate(src.getDepartureDate());
		tgt.setDeparture(src.getDeparture());
		tgt.setAppearances(src.getAppearances());
	}

	// XXX
	// @Override
	// public Page<FerryFlight> listAllFerryFlights(ReviewStatus reviewStatus, int page, int pageSize) {
	// return doListAllProducts(reviewStatus, page, pageSize);
	// }
	//
	// @Override
	// public long countAllFerryFlights(ReviewStatus reviewStatus) {
	// return doCountAllProducts(reviewStatus);
	// }
	//
	// @Override
	// public Page<FerryFlight> listTenantFerryFlights(String tenantId, ReviewStatus reviewStatus, int page,
	// int pageSize) {
	// return doListTenantProducts(tenantId, reviewStatus, page, pageSize);
	// }
	//
	// @Override
	// public long countTenantFerryFlights(String tenantId, ReviewStatus reviewStatus) {
	// return doCountTenantProducts(tenantId, reviewStatus);
	// }

	@Override
	public Page<FerryFlight> listFerryFlights(int page, int pageSize) {
		// return doListProductsForUsers(page, pageSize);
		// differ from doListProductsForUsers(page, pageSize) orderBy
		return Pages.adapt(ferryFlightRepository.findPublished(Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<FerryFlight> listFerryFlightsByDeparture(String departure, int page, int pageSize) {
		return Pages.adapt(
				ferryFlightRepository.findPublishedByDeparture(departure, Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<FerryFlight> listFerryFlightsByArrival(String arrival, int page, int pageSize) {
		return Pages
				.adapt(ferryFlightRepository.findPublishedByArrival(arrival, Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<FerryFlight> listFerryFlightsByFuzzyLocation(String location, int page, int pageSize) {
		return Pages
				.adapt(ferryFlightRepository.findFuzzyByLocation(location, Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public List<FerryFlight> listTop3FerryFlights(String departure) {
		if (Strings.isBlank(departure)) {
			return ferryFlightRepository.findTop3();
		}
		return ferryFlightRepository.findTop3ByDeparture(departure);
	}

	// XXX
	// @CacheEvict(cacheNames = CACHE_NAME, key = "#ferryFlightId")
	// @Override
	// public void deleteFerryFlight(String ferryFlightId) {
	// doDeleteProduct(ferryFlightId);
	// }
	//
	// @CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	// @Override
	// public void deleteFerryFlights(String tenantId) {
	// doDeleteProducts(tenantId);
	// }

	@Override
	protected Code productNotFoundCode() {
		return Codes.FERRYFLIGHT_NOT_FOUND;
	}

	@Override
	protected BaseProductRepository<FerryFlight> getProductRepository() {
		return ferryFlightRepository;
	}
}
