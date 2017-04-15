package net.aircommunity.platform.service.internal;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.*;
import net.aircommunity.platform.repository.AirTaxiRepository;
import net.aircommunity.platform.service.AirTaxiService;
import net.aircommunity.platform.service.AircraftService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import java.util.Set;

/**
 * Created by guankai on 15/04/2017.
 */
@Service
@Transactional
public class AirTaxiServiceImpl extends AbstractServiceSupport implements AirTaxiService {

    private static final String CACHE_NAME = "cache.airTaxi";

    @Resource
    private AirTaxiRepository airTaxiRepository;
    @Resource
    private AircraftService aircraftService;

    @Nonnull
    @Override
    public Page<AirTaxi> listAirTaxi(int page, int pageSize) {
        return Pages.adapt(airTaxiRepository.findAll(Pages.createPageRequest(page, pageSize)));
    }

    @Nonnull
    @Override
    public Page<AirTaxi> listAirTaxiByDeparture(String departure, int page, int pageSize) {
        return Pages.adapt(airTaxiRepository.findByDeparture(departure, Pages.createPageRequest(page, pageSize)));
    }

    @Cacheable(cacheNames = CACHE_NAME)
    @Nonnull
    @Override
    public AirTaxi findAirTaxi(String taxiId) {
        AirTaxi airTaxi = airTaxiRepository.findOne(taxiId);
        if (airTaxi == null) {
            throw new AirException(Codes.TAXI_NOT_FOUND, String.format("air taxi %s not found...", taxiId));
        }
        return airTaxi;
    }


    @Nonnull
    @Override
    public AirTaxi createAirTaxi(String tenantId, @Nonnull AirTaxi request) {
        AirTaxi airTaxi = new AirTaxi();
        Tenant tenant = findAccount(tenantId, Tenant.class);
        copyProperties(request, airTaxi);
        airTaxi.setVendor(tenant);
        return airTaxiRepository.save(airTaxi);
    }

    @Cacheable(cacheNames = CACHE_NAME, key = "#taxiId")
    @Nonnull
    @Override
    public AirTaxi updateAirTaxi(String taxiId, @Nonnull AirTaxi request) {
        AirTaxi airTaxi = findAirTaxi(taxiId);
        copyProperties(request, airTaxi);
        return airTaxiRepository.save(airTaxi);
    }

    @Nonnull
    @Override
    public Page<AirTaxi> listAirTaxiByTenant(String tenantId, int page, int pageSize) {
        Tenant tenant = findAccount(tenantId, Tenant.class);
        return Pages.adapt(airTaxiRepository.findByVendor(tenant, Pages.createPageRequest(page, pageSize)));
    }

    private void copyProperties(AirTaxi src, AirTaxi tgt) {
        tgt.setName(src.getName());
        tgt.setDescription(src.getDescription());
        tgt.setArrival(src.getArrival());
        tgt.setArrivalLoc(src.getArrivalLoc());
        tgt.setDeparture(src.getDeparture());
        tgt.setDepartLoc(src.getDepartLoc());
        Set<AircraftItem> aircraftItems = src.getAircraftItems();
        if (aircraftItems != null) {
            aircraftItems.stream().forEach(aircraftItem -> {
                Aircraft aircraft = aircraftItem.getAircraft();
                if (aircraft != null) {
                    aircraft = aircraftService.findAircraft(aircraft.getId());
                    aircraftItem.setAircraft(aircraft);
                }
            });
            tgt.setAircraftItems(src.getAircraftItems());
        }
    }
}
