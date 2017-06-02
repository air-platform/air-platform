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

import com.fasterxml.jackson.annotation.JsonView;

import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Product;
import net.aircommunity.platform.model.ProductFaq;
import net.aircommunity.platform.service.CommonProductService;

/**
 * Product RESTful API. <b>all permission</b> for ALL (NOTE: base resource class for subclass extending MUST be public)
 * 
 * @author Bin.Zhang
 */
public abstract class ProductResourceSupport<T extends Product> {

	@Resource
	protected CommonProductService commonProductService;

	// *****************
	// Product
	// *****************

	/**
	 * Find (this can replace the XxxResource.find(xxxId) that extends this class), but JUST use XxxResource instead of
	 * this for better apidoc generation for the moment (XXX just comment out to avoid resource path conflicting)
	 */
	// @GET
	// @PermitAll
	// @Path("{productId}")
	// @Produces(MediaType.APPLICATION_JSON)
	// @JsonView(View.Public.class)
	// public Product findProduct(@PathParam("productId") String productId) {
	// return commonProductService.findProduct(productId);
	// }

	// *****************
	// Product FAQ
	// *****************
	/**
	 * List all FAQs
	 */
	@GET
	@PermitAll
	@Path("{productId}/faqs")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Public.class)
	public Page<ProductFaq> listProductFaqs(@PathParam("productId") String productId,
			@QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		return commonProductService.listProductFaqs(productId, page, pageSize);
	}

	/**
	 * Find FAQ
	 */
	@GET
	@PermitAll
	@Path("{productId}/faqs/{productFaqId}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Public.class)
	public ProductFaq findProductFaq(@PathParam("productFaqId") String productFaqId) {
		return commonProductService.findProductFaq(productFaqId);
	}

}