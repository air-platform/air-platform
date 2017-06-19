package net.aircommunity.platform.rest;

import java.net.URI;
import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import io.swagger.annotations.Api;
import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.Promotion;
import net.aircommunity.platform.model.domain.Product.Category;
import net.aircommunity.platform.service.PromotionService;

/**
 * Promotion RESTful API allows list/find/query for ANYONE, and create/update/delete for ADMIN
 * 
 * @author Bin.Zhang
 */
@Api
@RESTful
@Path("promotions")
public class PromotionResource {
	private static final Logger LOG = LoggerFactory.getLogger(PromotionResource.class);

	@Resource
	private PromotionService promotionService;

	// ***********************
	// ANYONE
	// ***********************

	/**
	 * List all
	 */
	@GET
	@PermitAll
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Public.class)
	public List<Promotion> listAll(@QueryParam("category") Category category) {
		LOG.debug("list promotions for category: {}", category);
		return promotionService.listPromotions(category);
	}

	/**
	 * Find
	 */
	@GET
	@PermitAll
	@Path("{promotionId}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Public.class)
	public Promotion find(@PathParam("promotionId") String promotionId) {
		return promotionService.findPromotion(promotionId);
	}

	// *************
	// ADMIN ONLY
	// *************
	// XXX
	// NOTE: JsonView should be also applied to GET(none admin resources), once the Json view is enabled on Promotion
	// model
	// We haven't used any @JsonView on Promotion model, so @JsonView will just ignored

	/**
	 * Create
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@RolesAllowed(Roles.ROLE_ADMIN)
	@JsonView(JsonViews.Admin.class)
	public Response create(@NotNull @Valid Promotion promotion, @Context UriInfo uriInfo) {
		Promotion created = promotionService.createPromotion(promotion);
		URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
		LOG.debug("Created {}", uri);
		return Response.created(uri).build();
	}

	/**
	 * Update
	 */
	@PUT
	@Path("{promotionId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed(Roles.ROLE_ADMIN)
	@JsonView(JsonViews.Admin.class)
	public Promotion update(@PathParam("promotionId") String promotionId, @NotNull @Valid Promotion newPromotion) {
		return promotionService.updatePromotion(promotionId, newPromotion);
	}

	/**
	 * Delete
	 */
	@DELETE
	@Path("{promotionId}")
	@RolesAllowed(Roles.ROLE_ADMIN)
	public void delete(@PathParam("promotionId") String promotionId) {
		promotionService.deletePromotion(promotionId);
	}

	/**
	 * Delete all
	 */
	@DELETE
	@RolesAllowed(Roles.ROLE_ADMIN)
	public void deleteAll() {
		promotionService.deletePromotions();
	}

}
