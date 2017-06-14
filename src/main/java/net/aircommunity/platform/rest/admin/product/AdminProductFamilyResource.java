package net.aircommunity.platform.rest.admin.product;

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
import io.micro.common.Strings;
import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Product.Category;
import net.aircommunity.platform.model.ProductFamily;
import net.aircommunity.platform.model.Reviewable.ReviewStatus;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.rest.BaseResourceSupport;
import net.aircommunity.platform.service.ProductFamilyService;

/**
 * ProductFamily RESTful API for ADMIN
 * 
 * @author Bin.Zhang
 */
@RESTful
@RolesAllowed(Roles.ROLE_ADMIN)
public class AdminProductFamilyResource extends BaseResourceSupport {
	private static final Logger LOG = LoggerFactory.getLogger(AdminProductFamilyResource.class);

	@Resource
	private ProductFamilyService productFamilyService;

	/**
	 * Create
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Admin.class)
	public Response create(@QueryParam("tenant") String tenantId, @NotNull @Valid ProductFamily productFamily,
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
	@JsonView(JsonViews.Admin.class)
	public ProductFamily find(@PathParam("productFamilyId") String productFamilyId) {
		return productFamilyService.findProductFamily(productFamilyId);
	}

	/**
	 * List all
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Admin.class)
	public Page<ProductFamily> listAll(@QueryParam("tenant") String tenantId,
			@QueryParam("status") ReviewStatus reviewStatus, @QueryParam("category") Category category,
			@QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		LOG.debug("List all families with category: {}, page: {}, pageSize: {}", category, page, pageSize);
		if (Strings.isBlank(tenantId)) {
			return productFamilyService.listAllProductFamilies(reviewStatus, page, pageSize);
			// TODO?
			// return productFamilyService.listAllProductFamilies(reviewStatus,category, page, pageSize);
		}
		return productFamilyService.listTenantProductFamilies(tenantId, reviewStatus, category, page, pageSize);
	}

	/**
	 * List product families count to be reviewed
	 */
	@GET
	@Path("review/count")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonObject listToBeApproved(@QueryParam("tenant") String tenantId,
			@QueryParam("status") ReviewStatus reviewStatus) {
		if (Strings.isBlank(tenantId)) {
			return buildCountResponse(productFamilyService.countAllProductFamilies(reviewStatus));
		}
		return buildCountResponse(productFamilyService.countTenantProductFamilies(tenantId, reviewStatus));
	}

	/**
	 * Approve
	 */
	@POST
	@Path("{productFamilyId}/approve")
	public void approveProductFamily(@PathParam("productFamilyId") String productFamilyId) {
		productFamilyService.reviewProductFamily(productFamilyId, ReviewStatus.APPROVED, null);
	}

	/**
	 * Disapprove
	 */
	@POST
	@Path("{productFamilyId}/disapprove")
	@Consumes(MediaType.APPLICATION_JSON)
	public void disapproveProductFamily(@PathParam("productFamilyId") String productFamilyId,
			JsonObject rejectedReason) {
		String reason = getRejectedReason(rejectedReason);
		productFamilyService.reviewProductFamily(productFamilyId, ReviewStatus.REJECTED, reason);
	}

	/**
	 * Update
	 */
	@PUT
	@Path("{productFamilyId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Admin.class)
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
	public void deleteAll(@QueryParam("tenant") String tenantId) {
		productFamilyService.deleteProductFamilies(tenantId);
	}

}
