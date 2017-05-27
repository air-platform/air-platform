package net.aircommunity.platform.service.internal;

import java.util.Set;

import javax.annotation.Resource;

import net.aircommunity.platform.model.Aircraft;
import net.aircommunity.platform.model.AircraftAwareProduct;
import net.aircommunity.platform.model.SalesPackage;
import net.aircommunity.platform.service.AircraftService;

/**
 * Abstract Aircraft aware Product service support.
 * 
 * @author Bin.Zhang
 */
abstract class AircraftAwareService<T extends AircraftAwareProduct> extends AbstractProductService<T> {

	@Resource
	protected AircraftService aircraftService;

	protected Set<SalesPackage> applySalesPackages(Set<SalesPackage> srcSalesPackages) {
		if (srcSalesPackages != null) {
			srcSalesPackages.stream().forEach(salesPackage -> {
				Aircraft aircraft = salesPackage.getAircraft();
				if (aircraft != null) {
					aircraft = aircraftService.findAircraft(aircraft.getId());
					salesPackage.setAircraft(aircraft);
				}
			});
		}
		return srcSalesPackages;
	}

	@Override
	protected final void copyProperties(T src, T tgt) {
		tgt.setSalesPackages(applySalesPackages(src.getSalesPackages()));
		doCopyProperties(src, tgt);
	}

	protected void doCopyProperties(T src, T tgt) {
	}

}
