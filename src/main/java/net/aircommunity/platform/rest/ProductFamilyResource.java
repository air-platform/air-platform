package net.aircommunity.platform.rest;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonView;

import io.micro.annotation.RESTful;
import io.swagger.annotations.Api;
import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Product.Category;
import net.aircommunity.platform.model.ProductFamily;
import net.aircommunity.platform.service.ProductFamilyService;

/**
 * AirJet RESTful API allows list/find/query for ANYONE.
 * 
 * @author Bin.Zhang
 */
@Api
@RESTful
@PermitAll
@Path("product/families")
public class ProductFamilyResource {
	private static final Logger LOG = LoggerFactory.getLogger(ProductFamilyResource.class);

	@Resource
	private ProductFamilyService productFamilyService;

	// ***********************
	// ANYONE
	// ***********************

	/**
	 * List all
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Public.class)
	public Page<ProductFamily> listAll(@QueryParam("category") Category category,
			@QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		LOG.debug("List all product families with category: {}, page: {}, pageSize: {}", category, page, pageSize);
		if (category == null) {
			return productFamilyService.listProductFamilies(page, pageSize);
		}
		return productFamilyService.listProductFamiliesByCategory(category, page, pageSize);
	}

	/**
	 * Find
	 */
	@GET
	@Path("{productFamilyId}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Public.class)
	public ProductFamily find(@PathParam("productFamilyId") String productFamilyId) {
		return productFamilyService.findProductFamily(productFamilyId);
	}

}
