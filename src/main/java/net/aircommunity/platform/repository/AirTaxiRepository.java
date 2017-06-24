package net.aircommunity.platform.repository;

import java.util.List;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.micro.common.Strings;
import net.aircommunity.platform.model.domain.AirTaxi;
import net.aircommunity.platform.model.domain.AirTaxi_;
import net.aircommunity.platform.model.domain.FlightRoute_;
import net.aircommunity.platform.model.domain.Persistable_;

/**
 * Repository interface for {@link AirTaxi} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface AirTaxiRepository extends BaseProductRepository<AirTaxi> {

	/**
	 * List taxis with conditions via Specification (PUBLISHED product only)
	 */
	default Page<AirTaxi> findWithConditions(String departure, String arrival, String tenantId, Pageable pageable) {
		Specification<AirTaxi> spec = new Specification<AirTaxi>() {
			@Override
			public Predicate toPredicate(Root<AirTaxi> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate predicate = cb.conjunction();
				List<Expression<Boolean>> expressions = predicate.getExpressions();
				query.orderBy(cb.desc(root.get(AirTaxi_.rank)), cb.desc(root.get(AirTaxi_.score)));
				expressions.add(cb.equal(root.get(AirTaxi_.published), true)); // PUBLISHED
				if (Strings.isNotBlank(tenantId)) {
					expressions.add(cb.equal(root.get(AirTaxi_.vendor).get(Persistable_.id), tenantId));
				}
				if (Strings.isNotBlank(departure)) {
					expressions.add(cb.equal(root.get(AirTaxi_.flightRoute).get(FlightRoute_.departure), departure));
				}
				if (Strings.isNotBlank(arrival)) {
					expressions.add(cb.equal(root.get(AirTaxi_.flightRoute).get(FlightRoute_.arrival), arrival));
				}
				return predicate;
			}
		};
		return findAll(spec, pageable);
	}

	/**
	 * Find air taxi by departure or arrival (Fuzzy) (PUBLISHED product only)
	 * 
	 * @param location departure or arrival
	 * @param pageable the page request
	 * @return page of taxi
	 */
	@Query("SELECT t FROM #{#entityName} t WHERE t.published = TRUE AND "
			+ "( t.flightRoute.departure LIKE CONCAT(:location,'%') OR "
			+ "t.flightRoute.arrival LIKE CONCAT(:location,'%') ) ORDER BY t.rank DESC, t.score DESC")
	Page<AirTaxi> findFuzzyByLocation(@Param("location") String location, Pageable pageable);
	// @Query("SELECT t FROM #{#entityName} t WHERE t.published = TRUE AND "
	// + "( t.flightRoute.departure LIKE CONCAT('%',:location,'%') OR "
	// + "t.flightRoute.arrival LIKE CONCAT('%',:location,'%') ) ORDER BY t.rank DESC, t.score DESC")
	// Page<AirTaxi> findByFlightRouteDepartureContainingOrFlightRouteArrivalContainingOrderByRankAscScoreDesc(
	// String location, Pageable pageable);

	/**
	 * List all taxi arrivals for the given departure. (PUBLISHED product only)
	 * 
	 * @return a set of arrival
	 */
	@Query("SELECT DISTINCT t.flightRoute.arrival FROM #{#entityName} t where t.published = TRUE AND "
			+ "t.flightRoute.departure =:departure ")
	Set<String> findArrivalsFromDeparture(@Param("departure") String departure);

	/**
	 * List all taxi departures for the given arrival. (PUBLISHED product only)
	 * 
	 * @return a set of departures
	 */
	@Query("SELECT DISTINCT t.flightRoute.departure FROM #{#entityName} t where t.published = TRUE AND "
			+ "t.flightRoute.arrival =:arrival ")
	Set<String> findDeparturesToArrival(@Param("arrival") String arrival);

	/**
	 * Find air taxi by departure (WORKS)
	 * 
	 * @param departure
	 * @param pageable the page request
	 * @return page of taxi //
	 */
	// @Query("SELECT t FROM #{#entityName} t WHERE t.flightRoute.departure = :departure AND "
	// + "t.published = TRUE ORDER BY t.rank ASC, t.score DESC")
	// Page<AirTaxi> listAirTaxisByDepartureForUser(@Param("departure") String departure, Pageable pageable);
	// Page<AirTaxi> findByFlightRouteDepartureOrderByRankAscScoreDesc(String departure, Pageable pageable);

	/**
	 * Find air taxi by arrival (WORKS)
	 * 
	 * @param arrival
	 * @param pageable the page request
	 * @return page of taxi
	 */
	// @Query("SELECT t FROM #{#entityName} t WHERE t.flightRoute.arrival = :arrival AND "
	// + "t.published = TRUE ORDER BY t.rank ASC, t.score DESC")
	// Page<AirTaxi> listAirTaxisByArrivalForUser(@Param("arrival") String arrival, Pageable pageable);
	// Page<AirTaxi> findByFlightRouteArrivalOrderByRankAscScoreDesc(String arrival, Pageable pageable);
}
