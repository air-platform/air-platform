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

import com.fasterxml.jackson.annotation.JsonView;

import io.micro.annotation.RESTful;
import io.swagger.annotations.Api;
import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.domain.Banner;
import net.aircommunity.platform.model.domain.Product.Category;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.service.BannerService;

/**
 * Banner RESTful API allows list/find/query for ANYONE.
 * 
 * @author Bin.Zhang
 */
@Api
@RESTful
@PermitAll
@Path("banners")
public class BannerResource {
	private static final Logger LOG = LoggerFactory.getLogger(BannerResource.class);

	@Resource
	private BannerService bannerService;

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
	public Response listAll(@QueryParam("category") Category category, @QueryParam("page") @DefaultValue("0") int page,
			@QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		LOG.debug("List all banners with category: {}, page: {}, pageSize: {}", category, page, pageSize);
		if (page > 0) {
			return Response.ok(bannerService.listBanners(category, page, pageSize)).build();
		}
		if (category == null) {
			return Response.ok(bannerService.listBanners(Category.NONE)).build();
		}
		return Response.ok(bannerService.listBanners(category)).build();
	}

	/**
	 * Find
	 */
	@GET
	@PermitAll
	@Path("{bannerId}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Public.class)
	public Banner find(@PathParam("bannerId") String bannerId) {
		return bannerService.findBanner(bannerId);
	}

	// *************
	// ADMIN ONLY
	// *************
	// XXX
	// NOTE: JsonView should be also applied to GET(none admin resources), once the Json view is enabled on AirJet model
	// We haven't used any @JsonView on AirJet model, so @JsonView will just ignored

	/**
	 * Create
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@RolesAllowed(Roles.ROLE_ADMIN)
	@JsonView(JsonViews.Admin.class)
	public Response create(@NotNull @Valid Banner banner, @Context UriInfo uriInfo) {
		Banner created = bannerService.createBanner(banner);
		URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
		LOG.debug("Created {}", uri);
		return Response.created(uri).build();
	}

	/**
	 * Update
	 */
	@PUT
	@Path("{bannerId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed(Roles.ROLE_ADMIN)
	@JsonView(JsonViews.Admin.class)
	public Banner update(@PathParam("bannerId") String bannerId, @NotNull @Valid Banner newBanner) {
		return bannerService.updateBanner(bannerId, newBanner);
	}

	/**
	 * Delete
	 */
	@DELETE
	@Path("{bannerId}")
	@RolesAllowed(Roles.ROLE_ADMIN)
	public void delete(@PathParam("bannerId") String bannerId) {
		bannerService.deleteBanner(bannerId);
	}

	/**
	 * Delete all
	 */
	@DELETE
	@RolesAllowed(Roles.ROLE_ADMIN)
	public void deleteAll() {
		bannerService.deleteBanners();
	}

}
