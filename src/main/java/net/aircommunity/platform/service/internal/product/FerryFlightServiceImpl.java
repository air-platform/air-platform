package net.aircommunity.platform.service.internal.product;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.transaction.annotation.Transactional;

import io.micro.common.Strings;
import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.FerryFlight;
import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.repository.BaseProductRepository;
import net.aircommunity.platform.repository.FerryFlightRepository;
import net.aircommunity.platform.service.internal.Pages;
import net.aircommunity.platform.service.product.FerryFlightService;
import net.aircommunity.platform.service.product.annotation.ManagedProductService;

/**
 * FerryFlight service implementation.
 * 
 * @author Bin.Zhang
 */
@ManagedProductService(Product.Type.FERRYFLIGHT)
@Transactional(readOnly = true)
public class FerryFlightServiceImpl extends AbstractStandardProductService<FerryFlight> implements FerryFlightService {

	@Resource
	private FerryFlightRepository ferryFlightRepository;

	@Override
	public Page<FerryFlight> listFerryFlights(int page, int pageSize) {
		// return doListProductsForUsers(page, pageSize);
		// differ from doListProductsForUsers(page, pageSize) orderBy
		return Pages.adapt(ferryFlightRepository.findPublished(createPageRequest(page, pageSize)));
	}

	@Override
	public Page<FerryFlight> listFerryFlightsByDeparture(String departure, int page, int pageSize) {
		return Pages
				.adapt(ferryFlightRepository.findPublishedByDeparture(departure, createPageRequest(page, pageSize)));
	}

	@Override
	public Page<FerryFlight> listFerryFlightsByArrival(String arrival, int page, int pageSize) {
		return Pages.adapt(ferryFlightRepository.findPublishedByArrival(arrival, createPageRequest(page, pageSize)));
	}

	@Override
	public Page<FerryFlight> listFerryFlightsByFuzzyLocation(String location, int page, int pageSize) {
		return Pages.adapt(ferryFlightRepository.findFuzzyByLocation(location, createPageRequest(page, pageSize)));
	}

	@Override
	public List<FerryFlight> listTop3FerryFlights(String departure) {
		if (Strings.isBlank(departure)) {
			return ferryFlightRepository.findTop3();
		}
		return ferryFlightRepository.findTop3ByDeparture(departure);
	}

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

	@Override
	protected Code productNotFoundCode() {
		return Codes.FERRYFLIGHT_NOT_FOUND;
	}

	@Override
	protected BaseProductRepository<FerryFlight> getProductRepository() {
		return ferryFlightRepository;
	}
}
