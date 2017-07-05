package net.aircommunity.platform.rest.admin.product;

import java.net.URI;

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

import io.micro.common.Strings;
import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.model.domain.Reviewable.ReviewStatus;
import net.aircommunity.platform.rest.ProductFaqResourceSupport;
import net.aircommunity.platform.service.product.ProductFaqService;
import net.aircommunity.platform.service.product.StandardProductService;

/**
 * Base Tenant Product RESTful API for ADMIN.
 * 
 * @author Bin.Zhang
 */
public abstract class AdminProductResourceSupport<T extends Product> extends ProductFaqResourceSupport {
	private static final Logger LOG = LoggerFactory.getLogger(AdminProductResourceSupport.class);

	// *****************
	// Product
	// *****************

	/**
	 * Create (*)
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Admin.class)
	public Response createProduct(@QueryParam("tenant") String tenantId, @NotNull @Valid T product,
			@Context UriInfo uriInfo) {
		T created = getProductService().createProduct(tenantId, product);
		URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
		LOG.debug("Created: {}", uri);
		return Response.created(uri).build();
	}

	/**
	 * Find
	 */
	@GET
	@Path("{productId}")
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed(Roles.ROLE_ADMIN)
	@JsonView(JsonViews.Admin.class)
	public Product findProduct(@PathParam("productId") String productId) {
		return getProductService().findProduct(productId);
	}

	/**
	 * Update (*)
	 */
	@PUT
	@Path("{productId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Admin.class)
	public T updateProduct(@PathParam("productId") String productId, @NotNull @Valid T product) {
		return getProductService().updateProduct(productId, product);
	}

	/**
	 * List
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Admin.class)
	public Page<T> listProducts(@QueryParam("tenant") String tenantId, @QueryParam("status") ReviewStatus reviewStatus,
			@QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		if (Strings.isBlank(tenantId)) {
			return getProductService().listAllProducts(reviewStatus, page, pageSize);
		}
		return getProductService().listTenantProducts(tenantId, reviewStatus, page, pageSize);
	}

	/**
	 * Review count e.g. review/count?status=pending
	 */
	@GET
	@Path("review/count")
	@RolesAllowed(Roles.ROLE_ADMIN)
	@JsonView(JsonViews.Admin.class)
	@Produces(MediaType.APPLICATION_JSON)
	public JsonObject productReviewCount(@QueryParam("tenant") String tenantId,
			@QueryParam("status") ReviewStatus reviewStatus) {
		if (Strings.isBlank(tenantId)) {
			return buildCountResponse(getProductService().countAllProducts(reviewStatus));
		}
		return buildCountResponse(getProductService().countTenantProducts(tenantId, reviewStatus));
	}

	/**
	 * Update Rank
	 */
	@POST
	@Path("{productId}/rank")
	@JsonView(JsonViews.Admin.class)
	@RolesAllowed(Roles.ROLE_ADMIN)
	@Produces(MediaType.APPLICATION_JSON)
	public Product rankProduct(@PathParam("productId") String productId, @NotNull JsonObject rank) {
		int newRank = getRank(rank); // TODO how valid JsonObject
		return getProductService().updateProductRank(productId, newRank);
	}

	/**
	 * Publish on-sale
	 */
	@POST
	@Path("{productId}/publish")
	@JsonView(JsonViews.Admin.class)
	@RolesAllowed(Roles.ROLE_ADMIN)
	@Produces(MediaType.APPLICATION_JSON)
	public Product publishProduct(@PathParam("productId") String productId) {
		return getProductService().publishProduct(productId, true);
	}

	/**
	 * Unpublish off-sale
	 */
	@POST
	@Path("{productId}/unpublish")
	@JsonView(JsonViews.Admin.class)
	@RolesAllowed(Roles.ROLE_ADMIN)
	@Produces(MediaType.APPLICATION_JSON)
	public Product unpublishProduct(@PathParam("productId") String productId) {
		return getProductService().publishProduct(productId, false);
	}

	/**
	 * Approve a tenant product
	 */
	@POST
	@Path("{productId}/approve")
	@RolesAllowed(Roles.ROLE_ADMIN)
	public void approveProduct(@PathParam("productId") String productId) {
		getProductService().reviewProduct(productId, ReviewStatus.APPROVED, null);
	}

	/**
	 * Disapprove a tenant product (body: {"reason": "xxxx"} )
	 */
	@POST
	@Path("{productId}/disapprove")
	@RolesAllowed(Roles.ROLE_ADMIN)
	public void disapproveProduct(@PathParam("productId") String productId, @NotNull JsonObject rejectedReason) {
		String reason = getReason(rejectedReason);
		getProductService().reviewProduct(productId, ReviewStatus.REJECTED, reason);
	}

	/**
	 * Delete
	 */
	@DELETE
	@Path("{productId}")
	@RolesAllowed(Roles.ROLE_ADMIN)
	public void deleteProduct(@PathParam("productId") String productId) {
		getProductService().deleteProduct(productId);
	}

	/**
	 * Delete all
	 */
	@DELETE
	@RolesAllowed(Roles.ROLE_ADMIN)
	public void deleteProducts(@QueryParam("tenant") String tenantId) {
		getProductService().deleteProducts(tenantId);
	}

	@Override
	protected ProductFaqService getProductFaqService() {
		return getProductService();
	}

	protected abstract StandardProductService<T> getProductService();

}
