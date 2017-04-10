package net.aircommunity.platform.rest.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.ws.rs.NameBinding;

/**
 * Requires account owner or admin to access the resources.
 * 
 * @author Bin.Zhang
 */
@NameBinding
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface AllowResourceOwner {

}
