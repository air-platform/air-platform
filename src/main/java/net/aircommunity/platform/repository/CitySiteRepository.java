package net.aircommunity.platform.repository;

import net.aircommunity.platform.model.domain.CitySite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Repository interface for {@link CitySite} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 *
 * @author Xiangwen.Kong
 */
public interface CitySiteRepository extends JpaRepository<CitySite, String> {

    @Query("SELECT DISTINCT t.city FROM #{#entityName} t")
    List<String> findDistinctCity();

    List<CitySite> findByCityContainingIgnoreCase(String city);

    /**
     * Find cities by extract match
     *
     * @param city the city
     * @return a list of citysites
     */
    default List<CitySite> listCitySitesByCity(String city) {
        return findByCityContainingIgnoreCase(city);
    }

    List<CitySite> findByCity(String city);

    CitySite findByNameIgnoreCase(String name);

}
