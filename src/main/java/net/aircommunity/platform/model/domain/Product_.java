package net.aircommunity.platform.model.domain;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * Metamodel of product (edited manually)
 */
@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Course.class)
public abstract class Product_ {

	public static volatile SingularAttribute<Product, Boolean> published;
	public static volatile SingularAttribute<Product, Integer> rank;
	public static volatile SingularAttribute<Product, Double> score;
	public static volatile SingularAttribute<Product, Tenant> vendor;
	public static volatile SingularAttribute<Product, String> name;

}
