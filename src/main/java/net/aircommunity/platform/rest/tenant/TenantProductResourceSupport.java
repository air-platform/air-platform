package net.aircommunity.platform.rest.tenant;

import java.net.URI;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.json.JsonObject;
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
import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.model.domain.ProductFaq;
import net.aircommunity.platform.model.domain.Reviewable.ReviewStatus;
import net.aircommunity.platform.rest.BaseResourceSupport;
import net.aircommunity.platform.service.CommonProductService;

/**
 * Base Tenant Product RESTful API. <b>all permission</b> for ADMIN/TENANT
 * 
 * @author Bin.Zhang
 */
public abstract class TenantProductResourceSupport<T extends Product> extends BaseResourceSupport {
	private static final Logger LOG = LoggerFactory.getLogger(TenantProductResourceSupport.class);

	@Resource
	protected CommonProductService commonProductService;

	// *****************
	// Product
	// *****************
	/**
	 * Find
	 */
	@GET
	@Path("{productId}")
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_CUSTOMER_SERVICE, Roles.ROLE_TENANT })
	public Product findProduct(@PathParam("productId") String productId) {
		return commonProductService.findProduct(productId);
	}

	/**
	 * Update Rank (ADMIN)
	 */
	@POST
	@Path("{productId}/rank")
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed(Roles.ROLE_ADMIN)
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	public Product rankProduct(@PathParam("productId") String productId, JsonObject request) {
		int newRank = getRank(request);
		return commonProductService.updateProductRank(productId, newRank);
	}

	/**
	 * On Sale
	 */
	@POST
	@Path("{productId}/publish")
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_CUSTOMER_SERVICE, Roles.ROLE_TENANT })
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	public Product publishProduct(@PathParam("productId") String productId) {
		return commonProductService.publishProduct(productId, true);
	}

	/**
	 * Off Sale
	 */
	@POST
	@Path("{productId}/unpublish")
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_CUSTOMER_SERVICE, Roles.ROLE_TENANT })
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	public Product unpublishProduct(@PathParam("productId") String productId) {
		return commonProductService.publishProduct(productId, false);
	}

	/**
	 * Approve a tenant product (ADMIN & CS)
	 */
	@POST
	@Path("{productId}/approve")
	@RolesAllowed(Roles.ROLE_ADMIN)
	public void approveProduct(@PathParam("productId") String productId) {
		commonProductService.reviewProduct(productId, ReviewStatus.APPROVED, null);
	}

	/**
	 * Disapprove a tenant product (body: {"reason": "xxxx"} ) (ADMIN & CS)
	 */
	@POST
	@Path("{productId}/disapprove")
	@RolesAllowed(Roles.ROLE_ADMIN)
	public void disapproveProduct(@PathParam("productId") String productId, JsonObject rejectedReason) {
		String reason = getRejectedReason(rejectedReason);
		commonProductService.reviewProduct(productId, ReviewStatus.REJECTED, reason);
	}

	/**
	 * Delete
	 */
	@DELETE
	@Path("{productId}")
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_CUSTOMER_SERVICE, Roles.ROLE_TENANT })
	public void deleteProduct(@PathParam("productId") String productId) {
		commonProductService.deleteProduct(productId);
	}

	/**
	 * Delete all
	 */
	@DELETE
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_CUSTOMER_SERVICE, Roles.ROLE_TENANT })
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
	@Path("{productId}/faqs")
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_CUSTOMER_SERVICE, Roles.ROLE_TENANT })
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
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
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_CUSTOMER_SERVICE, Roles.ROLE_TENANT })
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
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
	@Path("{productId}/faqs/{productFaqId}")
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_CUSTOMER_SERVICE, Roles.ROLE_TENANT })
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
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
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_CUSTOMER_SERVICE, Roles.ROLE_TENANT })
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	public ProductFaq updateProductFaq(@PathParam("productFaqId") String productFaqId, @NotNull ProductFaq newFaq) {
		return commonProductService.updateProductFaq(productFaqId, newFaq);
	}

	/**
	 * Delete FAQ
	 */
	@DELETE
	@Path("{productId}/faqs/{productFaqId}")
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_CUSTOMER_SERVICE, Roles.ROLE_TENANT })
	public void deleteProductFaq(@PathParam("productFaqId") String productFaqId) {
		commonProductService.deleteProductFaq(productFaqId);
	}

	/**
	 * Delete all FAQ
	 */
	@DELETE
	@Path("{productId}/faqs")
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_CUSTOMER_SERVICE, Roles.ROLE_TENANT })
	public void deleteProductFaqs(@PathParam("productId") String productId) {
		commonProductService.deleteProductFaqs(productId);
	}
}
