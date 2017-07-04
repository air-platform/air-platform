package net.aircommunity.platform.model.domain;

import java.util.Date;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import net.aircommunity.platform.model.domain.Order.Status;
import net.aircommunity.platform.model.domain.Product.Type;

/**
 * Metamodel of OrderRef (edited manually)
 */
@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(OrderRef.class)
public abstract class OrderRef_ {

	public static volatile SingularAttribute<OrderRef, String> ownerId;
	public static volatile SingularAttribute<OrderRef, Status> status;
	public static volatile SingularAttribute<OrderRef, Type> type;
	public static volatile SingularAttribute<OrderRef, Date> creationDate;

}
