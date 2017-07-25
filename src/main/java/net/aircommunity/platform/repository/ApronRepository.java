package net.aircommunity.platform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import net.aircommunity.platform.model.domain.Apron;
import net.aircommunity.platform.model.domain.Apron.Type;

/**
 * Repository interface for {@link Apron} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface ApronRepository extends JpaRepository<Apron, String> {

	@Query("SELECT DISTINCT t.province FROM #{#entityName} t")
	List<String> findDistinctProvince();

	@Query("SELECT DISTINCT t.city FROM #{#entityName} t")
	List<String> findDistinctCity();

	// City
	default List<Apron> findPublishedByCity(String city, Type type) {
		if (type == null) {
			return findByCityAndPublishedTrue(city);
		}
		return findByCityAndTypeAndPublishedTrue(city, type);
	}

	List<Apron> findByCityAndPublishedTrue(String city);

	List<Apron> findByCityAndTypeAndPublishedTrue(String city, Type type);

	// Province
	default List<Apron> findPublishedByProvince(String province, Type type) {
		if (type == null) {
			return findByProvinceAndPublishedTrue(province);
		}
		return findByProvinceAndTypeAndPublishedTrue(province, type);
	}

	List<Apron> findByProvinceAndPublishedTrue(String city);

	List<Apron> findByProvinceAndTypeAndPublishedTrue(String city, Type type);
}
