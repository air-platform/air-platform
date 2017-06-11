package net.aircommunity.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.Passenger;

/**
 * Repository interface for {@link Passenger} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface PassengerRepository extends JpaRepository<Passenger, String> {

	Passenger findByOwnerIdAndIdentity(String userId, String identity);

}
