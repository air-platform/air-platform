package net.aircommunity.platform.rest;

import java.net.URI;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.json.JsonObject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micro.annotation.Authenticated;
import io.micro.annotation.RESTful;
import io.micro.common.Strings;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Role;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.Account;
import net.aircommunity.platform.model.domain.AircraftComment;
import net.aircommunity.platform.model.domain.Tenant;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.product.AircraftCommentService;
import net.aircommunity.platform.service.security.AccountService;

/**
 * AircraftComment RESTful API. NOTE: <b>all permission</b> for ADMIN/TENANT and <b>list/find/query</b> for ANYONE
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
public class AircraftCommentResource extends BaseResourceSupport {
	private static final Logger LOG = LoggerFactory.getLogger(AircraftCommentResource.class);

	@Resource
	private AccountService accountService;

	@Resource
	private AircraftCommentService aircraftCommentService;

	// ***********************
	// ANYONE
	// ***********************

	/**
	 * List all of an product
	 */
	@GET
	@PermitAll
	@Produces(MediaType.APPLICATION_JSON)
	public Page<AircraftComment> list(@PathParam("aircraftId") String aircraftId,
			@QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		return aircraftCommentService.listComments(aircraftId, page, pageSize);
	}

	/**
	 * Find
	 */
	@GET
	@PermitAll
	@Path("{commentId}")
	@Produces(MediaType.APPLICATION_JSON)
	public AircraftComment find(@PathParam("commentId") String commentId) {
		return aircraftCommentService.findComment(commentId);
	}

	/**
	 * Count
	 */
	@GET
	@PermitAll
	@Path("count")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonObject totalCount() {
		long count = aircraftCommentService.getTotalCommentsCount();
		return buildCountResponse(count);
	}

	// ***********************
	// ADMIN/TENANT
	// ***********************

	/**
	 * Create
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Authenticated
	public Response create(@NotNull @QueryParam("order") String orderId, @QueryParam("replyTo") String replyTo,
			@NotNull @Valid AircraftComment comment, @Context UriInfo uriInfo, @Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		if (Strings.isNotBlank(replyTo)) {
			Account replyToAccount = accountService.findAccount(replyTo);
			comment.setReplyTo(replyToAccount);
		}
		AircraftComment created = aircraftCommentService.createComment(accountId, orderId, comment);
		URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
		LOG.debug("Created {}", uri);
		return Response.created(uri).build();
	}

	/**
	 * Delete
	 */
	@DELETE
	@Authenticated
	@Path("{commentId}")
	public Response delete(@PathParam("commentId") String commentId, @Context SecurityContext context) {
		AircraftComment comment = aircraftCommentService.findComment(commentId);
		String accountId = context.getUserPrincipal().getName();
		Tenant tenant = comment.getAircraft().getVendor();
		Account owner = comment.getOwner();
		// allow comment owner, product owner (tenant, TODO customer service of a tenant?), admin
		boolean allowDeleteion = owner.getId().equals(accountId) || tenant.getId().equals(accountId)
				|| context.isUserInRole(Role.ADMIN.name());
		if (!allowDeleteion) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		aircraftCommentService.deleteComment(commentId);
		return Response.noContent().build();
	}

	/**
	 * Delete all
	 */
	@DELETE
	@RolesAllowed(Roles.ROLE_ADMIN)
	public Response deleteAll(@PathParam("aircraftId") String aircraftId, @Context SecurityContext context) {
		boolean allowDeleteion = context.isUserInRole(Role.ADMIN.name());
		if (!allowDeleteion) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		aircraftCommentService.deleteComments(aircraftId);
		return Response.noContent().build();
	}

}
