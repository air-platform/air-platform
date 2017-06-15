package net.aircommunity.platform.rest;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.micro.annotation.RESTful;
import io.swagger.annotations.Api;
import net.aircommunity.platform.model.Product;

/**
 * Common product RESTful API allows list/find/query for ANYONE.
 * 
 * @author Bin.Zhang
 */
@Api
@RESTful
@PermitAll
@Path("products")
public class ProductResource extends BaseResourceSupport {

	// *****************
	// Product
	// *****************
	/**
	 * Find
	 */
	@GET
	@Path("{productId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Product findProduct(@PathParam("productId") String productId) {
		return commonProductService.findProduct(productId);
	}

}
