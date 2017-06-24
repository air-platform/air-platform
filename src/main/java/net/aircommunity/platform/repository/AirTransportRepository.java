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
import net.aircommunity.platform.model.domain.AirTransport;
import net.aircommunity.platform.model.domain.AirTransport_;
import net.aircommunity.platform.model.domain.FlightRoute_;
import net.aircommunity.platform.model.domain.Persistable_;

/**
 * Repository interface for {@link AirTransport} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface AirTransportRepository extends BaseProductRepository<AirTransport> {

	/**
	 * List transports with conditions via Specification (PUBLISHED product only)
	 */
	default Page<AirTransport> findWithConditions(String familyId, String departure, String arrival, String tenantId,
			Pageable pageable) {
		Specification<AirTransport> spec = new Specification<AirTransport>() {
			@Override
			public Predicate toPredicate(Root<AirTransport> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate predicate = cb.conjunction();
				List<Expression<Boolean>> expressions = predicate.getExpressions();
				query.orderBy(cb.desc(root.get(AirTransport_.rank)), cb.desc(root.get(AirTransport_.score)));
				expressions.add(cb.equal(root.get(AirTransport_.published), true)); // PUBLISHED
				if (Strings.isNotBlank(familyId)) {
					expressions.add(cb.equal(root.get(AirTransport_.family).get(Persistable_.id), familyId));
				}
				if (Strings.isNotBlank(tenantId)) {
					expressions.add(cb.equal(root.get(AirTransport_.vendor).get(Persistable_.id), tenantId));
				}
				if (Strings.isNotBlank(departure)) {
					expressions
							.add(cb.equal(root.get(AirTransport_.flightRoute).get(FlightRoute_.departure), departure));
				}
				if (Strings.isNotBlank(arrival)) {
					expressions.add(cb.equal(root.get(AirTransport_.flightRoute).get(FlightRoute_.arrival), arrival));
				}
				return predicate;
			}
		};
		return findAll(spec, pageable);
	}

	/**
	 * Find all the published AirTransport for a family. (PUBLISHED product only), NOTE: should also available via
	 * <tt>findWithConditions(familyId)</tt>
	 * 
	 * @param familyId the familyId
	 * @param pageable the page request
	 * @return page of products
	 */
	Page<AirTransport> findByFamilyIdAndPublishedTrueOrderByRankAscScoreDesc(String familyId, Pageable pageable);

	/**
	 * List all transport arrivals of a family for the given departure. (PUBLISHED product only)
	 * 
	 * @return a set of arrival
	 */
	@Query("SELECT DISTINCT t.flightRoute.arrival FROM #{#entityName} t where t.published = TRUE AND "
			+ "t.family.id =:familyId AND t.flightRoute.departure =:departure ")
	Set<String> findArrivalsFromDeparture(@Param("familyId") String familyId, @Param("departure") String departure);

	/**
	 * List all transport departures of a family for the given arrival. (PUBLISHED product only)
	 * 
	 * @return a set of departures
	 */
	@Query("SELECT DISTINCT t.flightRoute.departure FROM #{#entityName} t where t.published = TRUE AND "
			+ "t.family.id =:familyId AND t.flightRoute.arrival =:arrival ")
	Set<String> findDeparturesToArrival(@Param("familyId") String familyId, @Param("arrival") String arrival);

	/**
	 * Find air trans by departure or arrival (Fuzzy) (PUBLISHED product only)
	 * 
	 * @param location departure or arrival
	 * @param pageable the page request
	 * @return page of trans
	 */
	@Query("SELECT t FROM #{#entityName} t WHERE t.published = TRUE AND "
			+ "( t.flightRoute.departure LIKE CONCAT(:location,'%') OR "
			+ "t.flightRoute.arrival LIKE CONCAT(:location,'%') ) ORDER BY t.rank ASC, t.score DESC")
	Page<AirTransport> findFuzzyByLocation(@Param("location") String location, Pageable pageable);
	// @Query("SELECT t FROM #{#entityName} t WHERE t.published = TRUE AND "
	// + "( t.flightRoute.departure LIKE CONCAT('%',:location,'%') OR "
	// + "t.flightRoute.arrival LIKE CONCAT('%',:location,'%') ) ORDER BY t.rank ASC, t.score DESC")
	// Page<AirTransport> findByFlightRouteDepartureContainingOrFlightRouteArrivalContainingOrderByRankAsc(String
	// location, Pageable pageable);

	/**
	 * List all transport families. (QUERY WORKS, just keep it for reference)
	 * 
	 * @return a list of families
	 */
	// @Query("SELECT DISTINCT t.family FROM #{#entityName} t")
	// Set<String> listFamilies();

	/**
	 * Find air trans by departure (QUERY WORKS, just keep it for reference)
	 * 
	 * @param departure
	 * @param pageable the page request
	 * @return page of trans
	 */
	// @Query("SELECT t FROM #{#entityName} t WHERE t.flightRoute.departure = :departure AND "
	// + "t.published = TRUE ORDER BY t.rank ASC, t.score DESC")
	// Page<AirTransport> listByDepartureForUser(@Param("departure") String departure, Pageable pageable);
	// Page<AirTransport> findByFlightRouteDepartureOrderByRankAsc(String departure, Pageable pageable);

	/**
	 * Find air trans by arrival (QUERY WORKS, just keep it for reference)
	 * 
	 * @param arrival
	 * @param pageable the page request
	 * @return page of trans
	 */
	// @Query("SELECT t FROM #{#entityName} t WHERE t.flightRoute.arrival = :arrival AND "
	// + "t.published = TRUE ORDER BY t.rank ASC, t.score DESC")
	// Page<AirTransport> listByArrivalForUser(@Param("arrival") String arrival, Pageable pageable);
	// Page<AirTransport> findByFlightRouteArrivalOrderByRankAsc(String arrival, Pageable pageable);

}
