package net.aircommunity.platform.service;

import net.aircommunity.platform.model.AirTaxi;
import net.aircommunity.platform.model.Page;

import javax.annotation.Nonnull;

/**
 * Created by guankai on 14/04/2017.
 */
public interface AirTaxiService {

    @Nonnull
    Page<AirTaxi> listAirTaxi(int page, int pageSize);

    @Nonnull
    Page<AirTaxi> listAirTaxiByDeparture(String departure, int page, int pageSize);

    @Nonnull
    AirTaxi createAirTaxi(String tenantId, @Nonnull AirTaxi request);

    @Nonnull
    AirTaxi updateAirTaxi(String taxiId, @Nonnull AirTaxi request);

    @Nonnull
    Page<AirTaxi> listAirTaxiByTenant(String tenantId, int page, int pageSize);

    @Nonnull
    AirTaxi findAirTaxi(String taxiId);


}
