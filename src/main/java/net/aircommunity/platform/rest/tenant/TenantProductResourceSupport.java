package net.aircommunity.platform.rest.tenant;

import java.net.URI;

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
import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.model.domain.Reviewable.ReviewStatus;
import net.aircommunity.platform.rest.ProductFaqResourceSupport;
import net.aircommunity.platform.service.product.ProductFaqService;
import net.aircommunity.platform.service.product.StandardProductService;

/**
 * Base Tenant Product RESTful API for TENANT, and also allow ADMIN
 * 
 * @author Bin.Zhang
 */
public abstract class TenantProductResourceSupport<T extends Product> extends ProductFaqResourceSupport {
	private static final Logger LOG = LoggerFactory.getLogger(TenantProductResourceSupport.class);

	// *****************
	// Product
	// *****************

	/**
	 * Create (*)
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	public Response createProduct(@PathParam("tenantId") String tenantId, @NotNull @Valid T product,
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
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
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
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	public T updateProduct(@PathParam("productId") String productId, @NotNull @Valid T newProduct) {
		return getProductService().updateProduct(productId, newProduct);
	}

	/**
	 * List
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	public Page<T> listProducts(@PathParam("tenantId") String tenantId, @QueryParam("status") ReviewStatus reviewStatus,
			@QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		return getProductService().listTenantProducts(tenantId, reviewStatus, page, pageSize);
	}

	/**
	 * Review count e.g. review/count?status=pending
	 */
	@GET
	@Path("review/count")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	public JsonObject productReviewCount(@PathParam("tenantId") String tenantId,
			@QueryParam("status") ReviewStatus reviewStatus) {
		return buildCountResponse(getProductService().countTenantProducts(tenantId, reviewStatus));
	}

	/**
	 * Publish on-sale
	 */
	@POST
	@Path("{productId}/publish")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	public Product publishProduct(@PathParam("productId") String productId) {
		return getProductService().publishProduct(productId, true);
	}

	/**
	 * Unpublish off-sale
	 */
	@POST
	@Path("{productId}/unpublish")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	public Product unpublishProduct(@PathParam("productId") String productId) {
		return getProductService().publishProduct(productId, false);
	}

	/**
	 * Delete
	 */
	@DELETE
	@Path("{productId}")
	public void deleteProduct(@PathParam("productId") String productId) {
		getProductService().deleteProduct(productId);
	}

	/**
	 * Delete all
	 */
	@DELETE
	public void deleteProducts(@PathParam("tenantId") String tenantId) {
		getProductService().deleteProducts(tenantId);
	}

	@Override
	protected ProductFaqService getProductFaqService() {
		return getProductService();
	}

	protected abstract StandardProductService<T> getProductService();

}
