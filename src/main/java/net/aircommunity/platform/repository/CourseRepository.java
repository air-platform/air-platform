package net.aircommunity.platform.repository;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

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

import io.micro.common.Strings;
import net.aircommunity.platform.model.domain.Course;
import net.aircommunity.platform.model.domain.Course_;
import net.aircommunity.platform.model.domain.Persistable_;

/**
 * Repository interface for {@link Course} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface CourseRepository extends BaseProductRepository<Course> {

	// all valid and none expired
	default Page<Course> findAllCourses(Pageable pageable) {
		return findByConditions(null, null, null, pageable);
	}

	default Page<Course> findBySchoolId(String schoolId, Pageable pageable) {
		return findWithConditions(null, null, schoolId, null, pageable);
	}

	default Page<Course> findByLocation(String location, Pageable pageable) {
		return findByConditions(location, null, null, pageable);
	}

	default Page<Course> findByConditions(String location, String aircraftType, String license, Pageable pageable) {
		return findWithConditions(location, aircraftType, null, license, pageable);
	}

	/**
	 * List courses with conditions via Specification (valid, not expired), normally: location + aircraftType (Using
	 * where)
	 */
	default Page<Course> findWithConditions(String location, String aircraftType, String schoolId, String license,
			Pageable pageable) {
		Specification<Course> spec = new Specification<Course>() {
			@Override
			public Predicate toPredicate(Root<Course> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate predicate = cb.conjunction();
				List<Expression<Boolean>> expressions = predicate.getExpressions();
				query.orderBy(cb.desc(root.get(Course_.rank)), cb.desc(root.get(Course_.startDate)),
						cb.desc(root.get(Course_.score)));
				expressions.add(cb.equal(root.get(Course_.published), true));
				if (Strings.isNotBlank(schoolId)) {
					expressions.add(cb.equal(root.get(Course_.school).get(Persistable_.id), schoolId));
				}
				if (Strings.isNotBlank(location)) {
					// XXX for fuzzy match if needed
					// expressions.add(cb.like(root.get(Course_.location), "%" + location + "%"));
					expressions.add(cb.equal(root.get(Course_.location), location));
				}
				if (Strings.isNotBlank(aircraftType)) {
					expressions.add(cb.equal(root.get(Course_.aircraftType), aircraftType));
				}
				if (Strings.isNotBlank(license)) {
					expressions.add(cb.equal(root.get(Course_.license), license));
				}
				expressions.add(cb.greaterThanOrEqualTo(root.get(Course_.endDate), new Date()/* now */));
				return predicate;
			}
		};
		return findAll(spec, pageable);
	}

	/**
	 * List all courses (USER)
	 */
	Page<Course> findByPublishedTrueOrderByRankDescStartDateDescScoreDesc(Pageable pageable);

	/**
	 * Hot Top 10
	 */
	default List<Course> findHotTop10(Date endDate) {
		return findTop10ByPublishedTrueAndEndDateGreaterThanEqualOrderByRankDescEnrollNumDesc(endDate);
	}

	List<Course> findTop10ByPublishedTrueAndEndDateGreaterThanEqualOrderByRankDescEnrollNumDesc(Date endDate);

	@Query("SELECT DISTINCT t.aircraftType FROM #{#entityName} t")
	Set<String> listAircraftTypes();

	@Query("SELECT DISTINCT t.license FROM #{#entityName} t")
	Set<String> listAircraftLicenses();

	@Query("SELECT DISTINCT t.location FROM #{#entityName} t")
	Set<String> listCourseLocations();

	// iterator over courses (For ADMIN ONLY)
	Stream<Course> findBySchoolId(String schoolId);

	long deleteBySchoolId(String schoolId);

	// *************************
	// XXX COMMENTED OUT NOT USED
	// *************************

	/**
	 * List courses for user (only valid courses) of a school
	 */
	// default Page<Course> findBySchoolId(String schoolId, Date endDate, Pageable pageable) {
	// return findBySchoolIdAndPublishedAndEndDateGreaterThanEqualOrderByRankAscStartDateDesc(schoolId, true, endDate,
	// pageable);
	// }

	// Page<Course> findBySchoolIdAndPublishedAndEndDateGreaterThanEqualOrderByRankAscStartDateDesc(String schoolId,
	// boolean published, Date endDate, Pageable pageable);

	/**
	 * List courses for user (only valid courses)
	 */
	// default Page<Course> findValidCourses(Date endDate, Pageable pageable) {
	// return findByPublishedAndEndDateGreaterThanEqualOrderByStartDateDesc(true, endDate, pageable);
	// }

	// Page<Course> findByPublishedAndEndDateGreaterThanEqualOrderByStartDateDesc(boolean published, Date endDate,
	// Pageable pageable);

	/**
	 * List courses for user (only valid courses filter by aircraftType)
	 */
	// default Page<Course> findValidCoursesByAircraftType(String aircraftType, Date endDate, Pageable pageable) {
	// return findByPublishedAndEndDateGreaterThanEqualAndAircraftTypeContainingOrderByStartDateDesc(true, endDate,
	// aircraftType, pageable);
	// }

	// Page<Course> findByPublishedAndEndDateGreaterThanEqualAndAircraftTypeContainingOrderByStartDateDesc(
	// boolean published, Date endDate, String aircraftType, Pageable pageable);

	/**
	 * List courses for user (only valid courses filter by location)
	 */
	// default Page<Course> findValidCoursesByLocation(String location, Date endDate, Pageable pageable) {
	// return findByPublishedAndEndDateGreaterThanEqualAndLocationContainingOrderByStartDateDesc(true, endDate,
	// location, pageable);
	// }

	// Page<Course> findByPublishedAndEndDateGreaterThanEqualAndLocationContainingOrderByStartDateDesc(boolean
	// published,
	// Date endDate, String location, Pageable pageable);

	// *************************
	// TODO REMOVE
	// *************************
	// Page<Course> findByVendorIdOrderByStartDateDesc(String tenantId, Pageable pageable);
	// For USER (always findByEndDateGreaterThan)
	// @Query("select t from Course t where t.endDate >= :now and t.school = :sc order by t.startDate desc")
	// Page<Course> findBySchool2(@Param("sc") School school, @Param("now") Date now, Pageable pageable);
	//
	// @Query("select t from Course t where t.endDate >= :now and t.airType like %:airType% order by t.startDate desc")
	// Page<Course> findValidCourses(@Param("airType") String airType, @Param("now") Date now, Pageable pageable);
	//
	// @Query("select t from #{#entityName} t where t.endDate >= :now and t.vendor = :te and t.airType like %:airType%
	// order by t.startDate desc")
	// Page<Course> findTenantValidCourses2(@Param("te") Tenant tenant, @Param("airType") String airType,
	// @Param("now") Date now, Pageable pageable);

}
