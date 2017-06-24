package net.aircommunity.platform.model.domain;

import java.util.Date;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * Metamodel of FerryFlight (edited manually)
 */
@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(FerryFlight.class)
public abstract class FerryFlight_ extends Product_ {

	public static volatile SingularAttribute<FerryFlight, String> departure;
	public static volatile SingularAttribute<FerryFlight, String> arrival;
	public static volatile SingularAttribute<FerryFlight, Date> departureDate;

}
