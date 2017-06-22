package net.aircommunity.platform.model.domain;

import java.util.Date;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * Metamodel of Course
 */
@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Course.class)
public abstract class Course_ extends Product_ {

	public static volatile SingularAttribute<Course, String> license;
	public static volatile SingularAttribute<Course, String> aircraftType;
	public static volatile SingularAttribute<Course, Date> endDate;
	public static volatile SingularAttribute<Course, Integer> totalNum;
	public static volatile SingularAttribute<Course, Integer> enrollNum;
	public static volatile SingularAttribute<Course, School> school;
	public static volatile SingularAttribute<Course, String> courseService;
	public static volatile SingularAttribute<Course, String> location;
	public static volatile SingularAttribute<Course, Date> startDate;
	public static volatile SingularAttribute<Course, String> enrollment;

}
