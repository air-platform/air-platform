package net.aircommunity.platform.service.internal;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.AirTour;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.repository.AirTourRepository;
import net.aircommunity.platform.service.AirTourService;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import javax.transaction.Transactional;

/**
 * Created by guankai on 14/04/2017.
 */
@Service
@Transactional
public class AirTourServiceImpl implements AirTourService {

    @Resource
    private AirTourRepository airTourRepository;

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

    @Override
    public AirTour getTourById(@Nonnull String tourId) {
        AirTour airTour = airTourRepository.findOne(tourId);
        if (airTour == null){
            throw new AirException(Codes.TOUR_NOT_FOUND,String.format("tour %s not found"));
        }
        return airTour;
    }

    @Nonnull
    @Override
    public AirTour createTour(@Nonnull AirTour request) {
        return null;
    }

    @Nonnull
    @Override
    public AirTour updateTour(@Nonnull AirTour request) {
        return null;
    }
}
