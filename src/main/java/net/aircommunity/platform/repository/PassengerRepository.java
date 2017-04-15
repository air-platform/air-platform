package net.aircommunity.platform.repository;

import net.aircommunity.platform.model.Passenger;
import net.aircommunity.platform.model.School;
import net.aircommunity.platform.model.Tenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * Created by guankai on 11/04/2017.
 */
public interface PassengerRepository extends JpaRepository<Passenger, String> {

}
