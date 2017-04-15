package net.aircommunity.platform.repository;

import net.aircommunity.platform.model.AirTour;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by guankai on 14/04/2017.
 */
public interface AirTourRepository extends BaseProductRepository<AirTour> {
    /**
     * 通过city查询产品
     * @param city
     * @param pageable
     * @return
     */
    Page<AirTour> findByCity(String city, Pageable pageable);
}
