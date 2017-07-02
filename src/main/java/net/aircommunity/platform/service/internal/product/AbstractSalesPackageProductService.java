package net.aircommunity.platform.service.internal.product;

import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.aircommunity.platform.model.domain.Aircraft;
import net.aircommunity.platform.model.domain.SalesPackage;
import net.aircommunity.platform.model.domain.SalesPackageProduct;
import net.aircommunity.platform.service.product.AircraftService;

/**
 * Abstract SalesPackage product service support.
 * 
 * @author Bin.Zhang
 */
abstract class AbstractSalesPackageProductService<T extends SalesPackageProduct>
		extends AbstractStandardProductService<T> {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractSalesPackageProductService.class);

	@Resource
	protected AircraftService aircraftService;

	@Override
	protected final void copyProperties(T src, T tgt) {
		LOG.debug("Copy properties src: {}", src);
		LOG.debug("Copy properties tgt: {}", tgt);
		Set<SalesPackage> tgtSalesPackages = tgt.getSalesPackages();
		LOG.debug("tgtSalesPackages original: {}", tgtSalesPackages);
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
				// NOTE: need to clean id to make it brand new, since we just fully replace old ones with new ones
				// otherwise we will got the following exception:
				// nested exception is org.hibernate.PersistentObjectException: detached entity passed to persist:
				// net.aircommunity.platform.model.domain.SalesPackage
				salesPackage.setId(null);
			});
		}
		return srcSalesPackages;
	}

	protected void doCopyProperties(T src, T tgt) {
	}
}
