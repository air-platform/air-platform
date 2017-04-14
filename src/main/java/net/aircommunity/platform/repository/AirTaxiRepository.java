package net.aircommunity.platform.repository;

import net.aircommunity.platform.model.AirTaxi;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by guankai on 14/04/2017.
 */
public interface AirTaxiRepository extends JpaRepository<AirTaxi, String> {
}
