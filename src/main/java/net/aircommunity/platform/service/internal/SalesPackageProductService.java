package net.aircommunity.platform.service.internal;

import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.aircommunity.platform.model.domain.Aircraft;
import net.aircommunity.platform.model.domain.SalesPackage;
import net.aircommunity.platform.model.domain.SalesPackageProduct;
import net.aircommunity.platform.repository.SalesPackageRepository;
import net.aircommunity.platform.service.AircraftService;

/**
 * Abstract SalesPackage product service support.
 * 
 * @author Bin.Zhang
 */
abstract class SalesPackageProductService<T extends SalesPackageProduct> extends AbstractProductService<T> {
	private static final Logger LOG = LoggerFactory.getLogger(SalesPackageProductService.class);

	@Resource
	protected SalesPackageRepository salesPackageRepository;

	@Resource
	protected AircraftService aircraftService;

	@Override
	protected final void copyProperties(T src, T tgt) {
		Set<SalesPackage> tgtSalesPackages = tgt.getSalesPackages();
		LOG.debug("tgtSalesPackages original: {}", tgtSalesPackages);
		// TODO REMOVE
		// for (SalesPackage sp : tgtSalesPackages) {
		// sp.setProduct(null);
		// sp.setAircraft(null);
		// salesPackageRepository.delete(sp);
		// LOG.debug("Find sp: {}, {}", sp.getId(), salesPackageRepository.findOne(sp.getId()));
		// }
		LOG.debug("Copy properties src: {}", src);
		LOG.debug("Copy properties tgt: {}", tgt);
		Set<SalesPackage> srcSalesPackages = src.getSalesPackages();
		LOG.debug("srcSalesPackages: {}", srcSalesPackages);
		tgt.setSalesPackages(applySalesPackages(srcSalesPackages));
		LOG.debug("tgtSalesPackages updated: {}", tgt.getSalesPackages());
		doCopyProperties(src, tgt);
	}

	private Set<SalesPackage> applySalesPackages(Set<SalesPackage> srcSalesPackages) {
		if (srcSalesPackages != null) {
			srcSalesPackages.stream().forEach(salesPackage -> {
				Aircraft aircraft = salesPackage.getAircraft();
				if (aircraft != null) {
					aircraft = aircraftService.findAircraft(aircraft.getId());
					salesPackage.setAircraft(aircraft);
				}
				// set to normalize
				salesPackage.setPrices(salesPackage.getPrices());
			});
		}
		return srcSalesPackages;
	}

	protected void doCopyProperties(T src, T tgt) {
	}
}
