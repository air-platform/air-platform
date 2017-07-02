package net.aircommunity.platform.rest;

import java.net.URI;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonView;

import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.ProductFaq;
import net.aircommunity.platform.service.product.ProductFaqService;

/**
 * Generic product FAQ management RESTful API support for (ADMIN and TENANT)
 * 
 * @author Bin.Zhang
 */
public abstract class ProductFaqResourceSupport extends BaseResourceSupport {
	private static final Logger LOG = LoggerFactory.getLogger(ProductFaqResourceSupport.class);

	/**
	 * List all FAQs
	 */
	@GET
	@Path("{productId}/faqs")
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_CUSTOMER_SERVICE, Roles.ROLE_TENANT })
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	public Page<ProductFaq> listProductFaqs(@PathParam("productId") String productId,
			@QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		return getProductFaqService().listProductFaqs(productId, page, pageSize);
	}

	/**
	 * Create FAQ
	 */
	@POST
	@Path("{productId}/faqs")
	@Consumes(MediaType.APPLICATION_JSON)
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_CUSTOMER_SERVICE, Roles.ROLE_TENANT })
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	public Response createProductFaq(@PathParam("productId") String productId, @NotNull @Valid ProductFaq faq,
			@Context UriInfo uriInfo) {
		ProductFaq created = getProductFaqService().createProductFaq(productId, faq);
		URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
		LOG.debug("Created: {}", uri);
		return Response.created(uri).build();
	}

	/**
	 * Find FAQ
	 */
	@GET
	@Path("{productId}/faqs/{productFaqId}")
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_CUSTOMER_SERVICE, Roles.ROLE_TENANT })
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	public ProductFaq findProductFaq(@PathParam("productFaqId") String productFaqId) {
		return getProductFaqService().findProductFaq(productFaqId);
	}

	/**
	 * Update FAQ
	 */
	@PUT
	@Path("{productId}/faqs/{productFaqId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_CUSTOMER_SERVICE, Roles.ROLE_TENANT })
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	public ProductFaq updateProductFaq(@PathParam("productFaqId") String productFaqId, @NotNull ProductFaq newFaq) {
		return getProductFaqService().updateProductFaq(productFaqId, newFaq);
	}

	/**
	 * Delete FAQ
	 */
	@DELETE
	@Path("{productId}/faqs/{productFaqId}")
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_CUSTOMER_SERVICE, Roles.ROLE_TENANT })
	public void deleteProductFaq(@PathParam("productFaqId") String productFaqId) {
		getProductFaqService().deleteProductFaq(productFaqId);
	}

	/**
	 * Delete all FAQ
	 */
	@DELETE
	@Path("{productId}/faqs")
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_CUSTOMER_SERVICE, Roles.ROLE_TENANT })
	public void deleteProductFaqs(@PathParam("productId") String productId) {
		getProductFaqService().deleteProductFaqs(productId);
	}

	protected abstract ProductFaqService getProductFaqService();

}
