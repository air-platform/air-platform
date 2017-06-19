package net.aircommunity.platform.rest;

import java.net.URI;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.json.Json;
import javax.json.JsonObject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
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
import io.swagger.annotations.Api;
import net.aircommunity.platform.model.domain.Account;
import net.aircommunity.platform.model.domain.Comment;
import net.aircommunity.platform.model.domain.Comment.Source;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Role;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.AccountService;
import net.aircommunity.platform.service.CommentService;

/**
 * Comment RESTful API. NOTE: <b>all permission</b> for ADMIN/TENANT and <b>list/find/query</b> for ANYONE
 * 
 * @author Bin.Zhang
 */
@Api
@RESTful
@Path("comments")
@AllowResourceOwner
public class CommentResource {
	private static final Logger LOG = LoggerFactory.getLogger(CommentResource.class);

	private static final String HEADER_COMMENT_ALLOWED = "Comment-Allowed";

	@Resource
	private AccountService accountService;

	@Resource
	private CommentService commentService;

	// ***********************
	// ANYONE
	// ***********************

	/**
	 * List all of an product
	 */
	@GET
	@PermitAll
	@Produces(MediaType.APPLICATION_JSON)
	public Page<Comment> list(@NotNull @QueryParam("product") String productId, @QueryParam("source") Source source,
			@QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		return commentService.listComments(productId, source, page, pageSize);
	}

	/**
	 * Find
	 */
	@GET
	@PermitAll
	@Path("{commentId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Comment find(@PathParam("commentId") String commentId) {
		return commentService.findComment(commentId);
	}

	/**
	 * Count
	 */
	@GET
	@PermitAll
	@Path("count")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonObject totalCount() {
		long count = commentService.getTotalCommentsCount();
		return Json.createObjectBuilder().add("count", count).build();
	}

	// TODO query by xxx

	// ***********************
	// ADMIN/TENANT
	// ***********************

	/**
	 * Test if can comment on an order
	 */
	@HEAD
	@Authenticated
	@Consumes(MediaType.APPLICATION_JSON)
	public Response canComment(@NotNull @QueryParam("order") String orderId, @Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		boolean canComment = commentService.isCommentAllowed(accountId, orderId);
		return Response.noContent().header(HEADER_COMMENT_ALLOWED, canComment).build();
	}

	/**
	 * Create: ?source=order&sourceId=xxx
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Authenticated
	public Response create(@NotNull @QueryParam("source") Source source,
			@NotNull @QueryParam("sourceId") String sourceId, @QueryParam("replyTo") String replyTo,
			@NotNull @Valid Comment comment, @Context UriInfo uriInfo, @Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		if (Strings.isNotBlank(replyTo)) {
			Account replyToAccount = accountService.findAccount(replyTo);
			comment.setReplyTo(replyToAccount);
		}
		Comment created = commentService.createComment(accountId, source, sourceId, comment);
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
		Comment comment = commentService.findComment(commentId);
		String accountId = context.getUserPrincipal().getName();
		if (!comment.getOwner().getId().equals(accountId) && !context.isUserInRole(Role.ADMIN.name())) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		commentService.deleteComment(commentId);
		return Response.noContent().build();
	}

	/**
	 * Delete all
	 */
	@DELETE
	@RolesAllowed(Roles.ROLE_ADMIN)
	public Response deleteAll(@QueryParam("product") String productId, @Context SecurityContext context) {
		if (!context.isUserInRole(Role.ADMIN.name())) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		commentService.deleteComments(productId);
		return Response.noContent().build();
	}

}
