package net.aircommunity.platform.service;

import net.aircommunity.platform.model.AirTour;
import net.aircommunity.platform.model.Page;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;

/**
 * Created by guankai on 14/04/2017.
 */
public interface AirTourService {

    @Nonnull
    Page<AirTour> getAllTours(int page, int pageSize);

    @Nonnull
    Page<AirTour> getToursByCity(@Nonnull String city, int page, int pageSize);

    @NotNull
    AirTour getTourById(@Nonnull String tourId);

    @Nonnull
    AirTour createTour(@Nonnull AirTour request);

    @Nonnull
    AirTour updateTour(@Nonnull AirTour request);
}
