package net.aircommunity.platform.repository;

import net.aircommunity.platform.model.AirTaxi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by guankai on 14/04/2017.
 */
public interface AirTaxiRepository extends BaseProductRepository<AirTaxi> {
    /**
     * find air taxi by departure
     * @param departure
     * @param pageable
     * @return
     */
    Page<AirTaxi> findByDeparture(String departure, Pageable pageable);
}
