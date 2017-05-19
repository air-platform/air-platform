package net.aircommunity.platform.rest;

import java.net.URI;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
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

import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Product;
import net.aircommunity.platform.model.ProductFaq;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.service.CommonProductService;

/**
 * Base Tenant Product RESTful API. <b>all permission</b> for ADMIN/TENANT
 * 
 * @author Bin.Zhang
 */
public abstract class BaseProductResource<T extends Product> {
	private static final Logger LOG = LoggerFactory.getLogger(BaseProductResource.class);

	@Resource
	protected CommonProductService commonProductService;

	// *****************
	// Product
	// *****************
	/**
	 * Find
	 */
	@GET
	@PermitAll
	@Path("{productId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Product findProduct(@PathParam("productId") String productId) {
		return commonProductService.findProduct(productId);
	}

	/**
	 * On Sale
	 */
	@POST
	@Path("{productId}/onsale")
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT })
	public Product putProductOnSale(@PathParam("productId") String productId) {
		return commonProductService.putProductOnSale(productId, true);
	}

	/**
	 * Off Sale
	 */
	@POST
	@Path("{productId}/offsale")
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT })
	public Product pullProductOffSale(@PathParam("productId") String productId) {
		return commonProductService.putProductOnSale(productId, false);
	}

	/**
	 * Delete
	 */
	@DELETE
	@Path("{productId}")
	public void deleteProduct(@PathParam("productId") String productId) {
		commonProductService.deleteProduct(productId);
	}

	/**
	 * Delete all
	 */
	@DELETE
	public void deleteProducts(@PathParam("tenantId") String tenantId) {
		commonProductService.deleteProducts(tenantId);
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
	public Page<ProductFaq> listProductFaqs(@PathParam("productId") String productId,
			@QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		return commonProductService.listProductFaqs(productId, page, pageSize);
	}

	/**
	 * Create FAQ
	 */
	@POST
	@Path("{productId}/faqs")
	@Consumes(MediaType.APPLICATION_JSON)
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT })
	public Response createProductFaq(@PathParam("productId") String productId, @NotNull @Valid ProductFaq faq,
			@Context UriInfo uriInfo) {
		ProductFaq created = commonProductService.createProductFaq(productId, faq);
		URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
		LOG.debug("Created: {}", uri);
		return Response.created(uri).build();
	}

	/**
	 * Find FAQ
	 */
	@GET
	@PermitAll
	@Path("{productId}/faqs/{productFaqId}")
	@Produces(MediaType.APPLICATION_JSON)
	public ProductFaq findProductFaq(@PathParam("productFaqId") String productFaqId) {
		return commonProductService.findProductFaq(productFaqId);
	}

	/**
	 * Update FAQ
	 */
	@PUT
	@Path("{productId}/faqs/{productFaqId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT })
	public ProductFaq updateProductFaq(@PathParam("productFaqId") String productFaqId, @NotNull ProductFaq newFaq) {
		return commonProductService.updateProductFaq(productFaqId, newFaq);
	}

	/**
	 * Delete FAQ
	 */
	@DELETE
	@Path("{productId}/faqs/{productFaqId}")
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT })
	public void deleteProductFaq(@PathParam("productFaqId") String productFaqId) {
		commonProductService.deleteProductFaq(productFaqId);
	}

	/**
	 * Delete all FAQ
	 */
	@DELETE
	@Path("{productId}/faqs")
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT })
	public void deleteProductFaqs(@PathParam("productId") String productId) {
		commonProductService.deleteProductFaqs(productId);
	}
}
