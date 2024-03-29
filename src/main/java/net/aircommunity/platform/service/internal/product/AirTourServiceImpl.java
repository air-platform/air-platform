package net.aircommunity.platform.service.internal.product;

import java.util.Set;

import javax.annotation.Resource;

import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.AirTour;
import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.repository.AirTourRepository;
import net.aircommunity.platform.repository.BaseProductRepository;
import net.aircommunity.platform.service.internal.Pages;
import net.aircommunity.platform.service.product.AirTourService;
import net.aircommunity.platform.service.product.annotation.ManagedProductService;

/**
 * AirTour Service implementation.
 * 
 * @author guankai
 */
@ManagedProductService(Product.Type.AIRTOUR)
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
		return Pages.adapt(airTourRepository.findByPublishedTrueAndCityOrderByRankDescScoreDesc(city,
				createPageRequest(page, pageSize)));
	}

	@Override
	public Page<AirTour> listAirToursByFuzzyCity(String city, int page, int pageSize) {
		return Pages.adapt(airTourRepository.findByFuzzyCity(city, createPageRequest(page, pageSize)));
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
