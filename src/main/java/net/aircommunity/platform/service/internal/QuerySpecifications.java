package net.aircommunity.platform.service.internal;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import net.aircommunity.platform.model.Product;

/**
 * Common domain Specifications used {@code JpaSpecificationExecutor}
 * 
 * @author Bin.Zhang
 */
final class QuerySpecifications {

	public static <T extends Product> Specification<T> specOf() {
		return new Specification<T>() {
			@Override
			public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate predicate = cb.conjunction();
				// List<Expression<Boolean>> expressions = predicate.getExpressions();
				//
				// Path<ReviewStatus> p = root.get("reviewStatus");
				//
				// query.orderBy(cb.desc(p));
				// if (Strings.isNotBlank(license)) {
				// expressions.add(cb.equal(root.get(Course_.location), license));
				// }
				// if (Strings.isNotBlank(license)) {
				// expressions.add(cb.equal(root.get(Course_.license), license));
				// }
				// if (Strings.isNotBlank(aircraftType)) {
				// expressions.add(cb.equal(root.get(Course_.aircraftType), aircraftType));
				// }
				// expressions.add(cb.greaterThanOrEqualTo(root.get(Course_.endDate), new Date()/* now */));
				return predicate;
			}
		};
	}

}