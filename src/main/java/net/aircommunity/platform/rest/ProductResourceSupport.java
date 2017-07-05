package net.aircommunity.platform.rest;

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
import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.model.domain.ProductFaq;
import net.aircommunity.platform.service.product.StandardProductService;

/**
 * Product RESTful API. <b>all permission</b> for ALL (NOTE: base resource class for subclass extending MUST be public)
 * 
 * @author Bin.Zhang
 */
public abstract class ProductResourceSupport<T extends Product> extends BaseResourceSupport {

	// *****************
	// Product
	// *****************

	@GET
	@PermitAll
	@Path("{productId}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Public.class)
	public T findProduct(@PathParam("productId") String productId) {
		return getProductService().findProduct(productId);
	}

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
		return getProductService().listProductFaqs(productId, page, pageSize);
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
		ProductFaq faq = getProductService().findProductFaq(productFaqId);
		getProductService().increaseProductFaqViews(productFaqId);
		return faq;
	}

	protected abstract StandardProductService<T> getProductService();
}
