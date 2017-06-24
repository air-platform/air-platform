package net.aircommunity.platform.repository;

import java.util.List;

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
import net.aircommunity.platform.model.domain.FerryFlight;
import net.aircommunity.platform.model.domain.FerryFlight_;

/**
 * Repository interface for {@link FerryFlight} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface FerryFlightRepository extends BaseProductRepository<FerryFlight> {

	default Page<FerryFlight> findPublished(Pageable pageable) {
		return findWithConditions(null, null, pageable);
	}

	default Page<FerryFlight> findPublishedByDeparture(String departure, Pageable pageable) {
		return findWithConditions(departure, null, pageable);
	}

	default Page<FerryFlight> findPublishedByArrival(String arrival, Pageable pageable) {
		return findWithConditions(null, arrival, pageable);
	}

	/**
	 * List taxis with conditions via Specification (PUBLISHED product only) (Using where;)
	 */
	default Page<FerryFlight> findWithConditions(String departure, String arrival, Pageable pageable) {
		Specification<FerryFlight> spec = new Specification<FerryFlight>() {
			@Override
			public Predicate toPredicate(Root<FerryFlight> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate predicate = cb.conjunction();
				List<Expression<Boolean>> expressions = predicate.getExpressions();
				query.orderBy(cb.desc(root.get(FerryFlight_.rank)), cb.desc(root.get(FerryFlight_.departureDate)),
						cb.desc(root.get(FerryFlight_.score)));
				expressions.add(cb.equal(root.get(FerryFlight_.published), true)); // PUBLISHED
				if (Strings.isNotBlank(departure)) {
					expressions.add(cb.equal(root.get(FerryFlight_.departure), departure));
				}
				if (Strings.isNotBlank(arrival)) {
					expressions.add(cb.equal(root.get(FerryFlight_.arrival), arrival));
				}
				return predicate;
			}
		};
		return findAll(spec, pageable);
	}

	// Top3
	default List<FerryFlight> findTop3() {
		return findTop3ByPublishedTrueOrderByRankDescDepartureDateDescScoreDesc();
	};

	List<FerryFlight> findTop3ByPublishedTrueOrderByRankDescDepartureDateDescScoreDesc();

	// Top3 by departure
	default List<FerryFlight> findTop3ByDeparture(String departure) {
		return findTop3ByPublishedTrueAndDepartureOrderByRankDescDepartureDateDescScoreDesc(departure);
	};

	List<FerryFlight> findTop3ByPublishedTrueAndDepartureOrderByRankDescDepartureDateDescScoreDesc(String departure);

	/**
	 * Find FerryFlight by departure or arrival (Using where; Using filesort)
	 * 
	 * @param location departure or arrival
	 * @param pageable the page request
	 * @return page of trans
	 */
	@Query("SELECT t FROM #{#entityName} t WHERE t.published = TRUE AND "
			+ "( t.departure LIKE CONCAT(:location,'%') OR "
			+ "t.arrival LIKE CONCAT(:location,'%') ) ORDER BY t.rank DESC, t.departureDate DESC, t.score DESC")
	Page<FerryFlight> findFuzzyByLocation(@Param("location") String location, Pageable pageable);
	// @Query("SELECT t FROM #{#entityName} t WHERE t.published = TRUE AND "
	// + "( t.departure LIKE CONCAT('%',:location,'%') OR "
	// + "t.arrival LIKE CONCAT('%',:location,'%') ) ORDER BY t.rank DESC, t.departureDate DESC, t.score DESC")
	// Page<FerryFlight> findByDepartureContainingOrArrivalContainingOrderByRankAsc(String location, Pageable pageable);

	/**
	 * PUBLISHED state (replaced by findWithConditions)
	 */
	// Page<FerryFlight> findByPublishedTrueOrderByRankDescDepartureDateDescScoreDesc(Pageable pageable);

	/**
	 * Find all by departure. (replaced by findWithConditions)
	 * 
	 * @param departure the departure
	 * @param pageable the page request
	 * @return page of FerryFlight
	 */
	// @Query("SELECT t FROM #{#entityName} t WHERE t.departure = :departure AND "
	// + "t.published = TRUE ORDER BY t.rank DESC, t.departureDate DESC, t.score DESC")
	// Page<FerryFlight> listByDepartureForUser(@Param("departure") String departure, Pageable pageable);

	/**
	 * Find all by arrival. (replaced by findWithConditions)
	 * 
	 * @param arrival the arrival
	 * @param pageable the page request
	 * @return page of FerryFlight
	 */
	// @Query("SELECT t FROM #{#entityName} t WHERE t.arrival = :arrival AND "
	// + "t.published = TRUE ORDER BY t.rank DESC, t.departureDate DESC, t.score DESC")
	// Page<FerryFlight> listByArrivalForUser(@Param("arrival") String arrival, Pageable pageable);

}
