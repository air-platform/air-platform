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

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.Product.Category;
import net.aircommunity.platform.model.domain.ProductFamily;
import net.aircommunity.platform.model.domain.Reviewable.ReviewStatus;
import net.aircommunity.platform.rest.BaseResourceSupport;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.product.ProductFamilyService;

/**
 * ProductFamily RESTful API for ADMIN/TENANT
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT, Roles.ROLE_CUSTOMER_SERVICE })
public class TenantProductFamilyResource extends BaseResourceSupport {
	private static final Logger LOG = LoggerFactory.getLogger(TenantProductFamilyResource.class);

	@Resource
	private ProductFamilyService productFamilyService;

	/**
	 * Create
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	public Response create(@PathParam("tenantId") String tenantId, @NotNull @Valid ProductFamily productFamily,
			@Context UriInfo uriInfo) {
		ProductFamily created = productFamilyService.createProductFamily(tenantId, productFamily);
		URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
		LOG.debug("Created {}", uri);
		return Response.created(uri).build();
	}

	/**
	 * Find
	 */
	@GET
	@Path("{productFamilyId}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	public ProductFamily find(@PathParam("productFamilyId") String productFamilyId) {
		return productFamilyService.findProductFamily(productFamilyId);
	}

	/**
	 * List all
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	public Page<ProductFamily> listAll(@PathParam("tenantId") String tenantId,
			@QueryParam("status") ReviewStatus reviewStatus, @QueryParam("category") Category category,
			@QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		LOG.debug("List all families with category: {}, page: {}, pageSize: {}", category, page, pageSize);
		return productFamilyService.listTenantProductFamilies(tenantId, reviewStatus, category, page, pageSize);
	}

	/**
	 * List product families count to be reviewed
	 */
	@GET
	@Path("review/count")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonObject reviewCount(@PathParam("tenantId") String tenantId,
			@QueryParam("status") ReviewStatus reviewStatus) {
		return buildCountResponse(productFamilyService.countTenantProductFamilies(tenantId, reviewStatus));
	}

	/**
	 * Update
	 */
	@PUT
	@Path("{productFamilyId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	public ProductFamily update(@PathParam("productFamilyId") String productFamilyId,
			@NotNull @Valid ProductFamily newProductFamily) {
		return productFamilyService.updateProductFamily(productFamilyId, newProductFamily);
	}

	/**
	 * Delete
	 */
	@DELETE
	@Path("{productFamilyId}")
	public void delete(@PathParam("productFamilyId") String productFamilyId) {
		productFamilyService.deleteProductFamily(productFamilyId);
	}

	/**
	 * Delete all
	 */
	@DELETE
	public void deleteAll(@PathParam("tenantId") String tenantId) {
		productFamilyService.deleteProductFamilies(tenantId);
	}

}
