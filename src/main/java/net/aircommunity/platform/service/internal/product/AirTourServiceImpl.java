package net.aircommunity.platform.service.internal.product;

import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.AirTour;
import net.aircommunity.platform.repository.AirTourRepository;
import net.aircommunity.platform.repository.BaseProductRepository;
import net.aircommunity.platform.service.internal.Pages;
import net.aircommunity.platform.service.product.AirTourService;

/**
 * AirTour Service implementation.
 * 
 * @author guankai
 */
@Service
@Transactional(readOnly = true)
public class AirTourServiceImpl extends AbstractSalesPackageProductService<AirTour> implements AirTourService {

	@Resource
	private AirTourRepository airTourRepository;

	@Override
	public Set<String> listAirTourCities() {
		return airTourRepository.listCities();
	}

	@Override
	public Page<AirTour> listAirToursByCity(String city, int page, int pageSize) {
		// NOTE: the first 1 has better performance, but less match result
		// return Pages.adapt(
		// airTourRepository.findByCityStartingWithIgnoreCase(city, createPageRequest(page, pageSize)));
		return Pages.adapt(airTourRepository.findByCityContainingIgnoreCase(city, createPageRequest(page, pageSize)));
	}

	@Override
	protected void doCopyProperties(AirTour src, AirTour tgt) {
		tgt.setCity(src.getCity());
		tgt.setTourDistance(src.getTourDistance());
		tgt.setBoardingLocation(src.getBoardingLocation());
		tgt.setTourPoint(src.getTourPoint());
		tgt.setTourShow(src.getTourShow());
		tgt.setTourTime(src.getTourTime());
		tgt.setTraffic(src.getTraffic());
		tgt.setTourRoute(src.getTourRoute());
	}

	@Override
	protected Code productNotFoundCode() {
		return Codes.TOUR_NOT_FOUND;
	}

	@Override
	protected BaseProductRepository<AirTour> getProductRepository() {
		return airTourRepository;
	}
}
