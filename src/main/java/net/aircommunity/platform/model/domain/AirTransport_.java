package net.aircommunity.platform.model.domain;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * Metamodel of AirTransport (edited manually)
 */
@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(AirTransport.class)
public abstract class AirTransport_ extends Product_ {

	public static volatile SingularAttribute<AirTransport, Integer> timeEstimation;
	public static volatile SingularAttribute<AirTransport, ProductFamily> family;
	public static volatile SingularAttribute<AirTransport, FlightRoute> flightRoute;

}
