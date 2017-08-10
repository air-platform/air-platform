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

	// **********
	// City
	// **********
	/**
	 * Find cities by fuzzy match
	 * 
	 * @param city the city
	 * @param type the apron type
	 * @return a list of aprons
	 */
	default List<Apron> findPublishedByFuzzyCity(String city, Type type) {
		if (type == null) {
			return findByCityContainingIgnoreCaseAndPublishedTrue(city);
		}
		return findByCityContainingIgnoreCaseAndTypeAndPublishedTrue(city, type);
	}

	List<Apron> findByCityContainingIgnoreCaseAndPublishedTrue(String city);

	List<Apron> findByPublishedTrue();

	List<Apron> findByCityContainingIgnoreCaseAndTypeAndPublishedTrue(String city, Type type);

	/**
	 * Find cities by extract match
	 * 
	 * @param city the city
	 * @param type the apron type
	 * @return a list of aprons
	 */
	default List<Apron> findPublishedByCity(String city, Type type) {
		if (type == null) {
			return findByCityAndPublishedTrue(city);
		}
		return findByCityAndTypeAndPublishedTrue(city, type);
	}

	List<Apron> findByCityAndPublishedTrue(String city);

	List<Apron> findByCityAndTypeAndPublishedTrue(String city, Type type);

	// **********
	// Province
	// **********

	/**
	 * Find provinces by fuzzy match
	 * 
	 * @param province the province
	 * @param type the apron type
	 * @return a list of aprons
	 */
	default List<Apron> findPublishedByFuzzyProvince(String province, Type type) {
		if (type == null) {
			return findByProvinceContainingIgnoreCaseAndPublishedTrue(province);
		}
		return findByProvinceContainingIgnoreCaseAndTypeAndPublishedTrue(province, type);
	}

	List<Apron> findByProvinceContainingIgnoreCaseAndPublishedTrue(String province);

	List<Apron> findByProvinceContainingIgnoreCaseAndTypeAndPublishedTrue(String province, Type type);

	/**
	 * Find provinces by extract match
	 * 
	 * @param province the province
	 * @param type the apron type
	 * @return a list of aprons
	 */
	default List<Apron> findPublishedByProvince(String province, Type type) {
		if (type == null) {
			return findByProvinceAndPublishedTrue(province);
		}
		return findByProvinceAndTypeAndPublishedTrue(province, type);
	}

	List<Apron> findByProvinceAndPublishedTrue(String province);

	List<Apron> findByProvinceAndTypeAndPublishedTrue(String province, Type type);
}
