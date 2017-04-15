package net.aircommunity.platform.service.internal;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.*;
import net.aircommunity.platform.repository.AirTourRepository;
import net.aircommunity.platform.service.AirTourService;
import net.aircommunity.platform.service.AircraftService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.Set;

/**
 * Created by guankai on 14/04/2017.
 */
@Service
@Transactional
public class AirTourServiceImpl extends AbstractServiceSupport implements AirTourService {

    private static final String CACHE_NAME = "cache.airTour";

    @Resource
    private AirTourRepository airTourRepository;
    @Resource
    private AircraftService aircraftService;

    @Nonnull
    @Override
    public Page<AirTour> getAllTours(int page, int pageSize) {
        return Pages.adapt(airTourRepository.findAll(Pages.createPageRequest(page, pageSize)));
    }

    @Nonnull
    @Override
    public Page<AirTour> getToursByCity(@Nonnull String city, int page, int pageSize) {
        return Pages.adapt(airTourRepository.findByCity(city, Pages.createPageRequest(page, pageSize)));
    }

    @Cacheable(cacheNames = CACHE_NAME)
    @Override
    public AirTour getTourById(@Nonnull String tourId) {
        AirTour airTour = airTourRepository.findOne(tourId);
        if (airTour == null) {
            throw new AirException(Codes.TOUR_NOT_FOUND, String.format("tour %s not found"));
        }
        return airTour;
    }

    @Nonnull
    @Override
    public AirTour createTour(@Nonnull AirTour request, @Nonnull String tenantId) {
        Tenant tenant = findAccount(tenantId, Tenant.class);
        AirTour airTour = new AirTour();
        copyProperties(request, airTour);
        airTour.setVendor(tenant);
        return airTourRepository.save(airTour);
    }

    @Cacheable(cacheNames = CACHE_NAME, key = "#tourId")
    @Nonnull
    @Override
    public AirTour updateTour(@Nonnull String tourId, @Nonnull AirTour request) {
        AirTour airTour = getTourById(tourId);
        copyProperties(request, airTour);
        return airTourRepository.save(airTour);
    }

    @Nonnull
    @Override
    public Page<AirTour> getToursByTenant(@Nonnull String tenantId, int page, int pageSize) {
        Tenant tenant = findAccount(tenantId, Tenant.class);
        return Pages.adapt(airTourRepository.findByVendor(tenant, Pages.createPageRequest(page, pageSize)));
    }

    private void copyProperties(AirTour src, AirTour tgt) {
        tgt.setCity(src.getCity());
        tgt.setTourDistance(src.getTourDistance());
        tgt.setBoardingLoc(src.getBoardingLoc());
        tgt.setTourPoint(src.getTourPoint());
        tgt.setTourShow(src.getTourShow());
        tgt.setTourTime(src.getTourTime());
        tgt.setTraffic(src.getTraffic());
        tgt.setName(src.getName());
        tgt.setDescription(src.getDescription());
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
