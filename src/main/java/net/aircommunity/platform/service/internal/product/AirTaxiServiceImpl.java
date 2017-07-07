package net.aircommunity.platform.service.internal.product;

import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.AirTaxi;
import net.aircommunity.platform.repository.AirTaxiRepository;
import net.aircommunity.platform.repository.BaseProductRepository;
import net.aircommunity.platform.service.internal.Pages;
import net.aircommunity.platform.service.product.AirTaxiService;

/**
 * AirTaxiService implementation
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional(readOnly = true)
public class AirTaxiServiceImpl extends AbstractSalesPackageProductService<AirTaxi> implements AirTaxiService {

	@Resource
	private AirTaxiRepository airTaxiRepository;

	@Override
	public Set<String> listArrivalsFromDeparture(String departure) {
		return airTaxiRepository.findArrivalsFromDeparture(departure);
	}

	@Override
	public Set<String> listDeparturesToArrival(String arrival) {
		return airTaxiRepository.findDeparturesToArrival(arrival);
	}

	@Override
	public Page<AirTaxi> listAirTaxisWithConditions(String departure, String arrival, String tenantId, int page,
			int pageSize) {
		return Pages.adapt(
				airTaxiRepository.findWithConditions(departure, arrival, tenantId, createPageRequest(page, pageSize)));
	}

	@Override
	public Page<AirTaxi> listAirTaxisByFuzzyLocation(String location, int page, int pageSize) {
		return Pages.adapt(airTaxiRepository.findFuzzyByLocation(location, createPageRequest(page, pageSize)));
	}

	@Override
	protected void doCopyProperties(AirTaxi src, AirTaxi tgt) {
		tgt.setFlightRoute(src.getFlightRoute());
		tgt.setDistance(src.getDistance());
		tgt.setDuration(src.getDuration());
	}

	@Override
	protected Code productNotFoundCode() {
		return Codes.TAXI_NOT_FOUND;
	}

	@Override
	protected BaseProductRepository<AirTaxi> getProductRepository() {
		return airTaxiRepository;
	}
}
