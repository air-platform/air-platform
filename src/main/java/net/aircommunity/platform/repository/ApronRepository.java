package net.aircommunity.platform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.domain.Apron;

/**
 * Repository interface for {@link Apron} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface ApronRepository extends JpaRepository<Apron, String> {

	List<Apron> findByCity(String city);

	List<Apron> findByCityAndPublishedTrue(String city);

	List<Apron> findByCityAndPublished(String city, boolean published);
}
