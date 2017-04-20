package net.aircommunity.platform.service.internal;

import java.util.Set;

import javax.annotation.Resource;

import net.aircommunity.platform.model.Aircraft;
import net.aircommunity.platform.model.AircraftAwareProduct;
import net.aircommunity.platform.model.AircraftItem;
import net.aircommunity.platform.service.AircraftService;

/**
 * Abstract Aircraft aware Product service support.
 * 
 * @author Bin.Zhang
 */
abstract class AircraftAwareService<T extends AircraftAwareProduct> extends AbstractProductService<T> {

	@Resource
	protected AircraftService aircraftService;

	protected Set<AircraftItem> applyAircraftItems(Set<AircraftItem> srcAircraftItems) {
		if (srcAircraftItems != null) {
			srcAircraftItems.stream().forEach(aircraftItem -> {
				Aircraft aircraft = aircraftItem.getAircraft();
				if (aircraft != null) {
					aircraft = aircraftService.findAircraft(aircraft.getId());
					aircraftItem.setAircraft(aircraft);
				}
			});
		}
		return srcAircraftItems;
	}

	@Override
	protected final void copyProperties(T src, T tgt) {
		tgt.setAircraftItems(applyAircraftItems(src.getAircraftItems()));
		doCopyProperties(src, tgt);
	}

	protected void doCopyProperties(T src, T tgt) {
	}

}
