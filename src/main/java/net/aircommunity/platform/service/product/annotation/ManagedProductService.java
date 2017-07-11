package net.aircommunity.platform.service.product.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

import net.aircommunity.platform.model.domain.Product;

/**
 * Indicates that an annotated class is a product "Service".
 *
 * @author Bin.Zhang
 */
@Inherited // -> IMPORTANT, otherwise we cannot get this via getClass().getAnnotation(ManagedProductService.class)
@Component
@Documented
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ManagedProductService {

	/**
	 * The value may indicate the type of product this product service manages
	 * 
	 * @return the type of product
	 */
	Product.Type value();

}
