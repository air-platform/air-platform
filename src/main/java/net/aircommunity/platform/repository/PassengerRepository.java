package net.aircommunity.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.Passenger;


/**
 * Created by guankai on 11/04/2017.
 */
public interface PassengerRepository extends JpaRepository<Passenger, String> {

}
