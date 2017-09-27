package net.aircommunity.platform.rest;

import com.fasterxml.jackson.annotation.JsonView;
import io.micro.annotation.RESTful;
import io.swagger.annotations.Api;
import java.net.URI;
import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.json.JsonObject;
import javax.json.JsonString;
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
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.Account;
import net.aircommunity.platform.model.domain.ActivityMessage;
import net.aircommunity.platform.model.domain.Reviewable.ReviewStatus;
import net.aircommunity.platform.model.domain.Tenant;
import net.aircommunity.platform.service.ActivityMessageService;
import net.aircommunity.platform.service.security.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Apron RESTful API allows list/find/query for ANYONE.
 *
 * @author Xiangwen.Kong
 */
@Api
@RESTful
@PermitAll
@Path("activity-messages")
public class ActivityMessageResource {
	private static final Logger LOG = LoggerFactory.getLogger(ActivityMessageResource.class);

	/**
	 * Reject reason, cancel, refund reason etc.
	 */
	private static final String JSON_PROP_REASON = "reason";

	private String getJsonString(JsonObject request, String name) {
		if (request == null) {
			return null;
		}
		JsonString str = request.getJsonString(name);
		if (str == null) {
			return null;
		}
		return str.getString();
	}

	protected String getReason(JsonObject request) { // TODO how to valid JsonObject using bval
		return getJsonString(request, JSON_PROP_REASON);
	}

	@Resource
	private ActivityMessageService activityMessageService;

	@Resource
	private AccountService accountService;

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
	public Response listAll(@QueryParam("page") @DefaultValue("1") int page,
							@QueryParam("pageSize") @DefaultValue("10") int pageSize, @Context SecurityContext context) {

		if (context.isUserInRole(Roles.ROLE_ADMIN) || context.isUserInRole(Roles.ROLE_TENANT)) {
			return Response.ok(activityMessageService.listActivityMessages(page, pageSize)).build();
		}
		else {
			String userName = context.getUserPrincipal().getName();
			return Response.ok(activityMessageService.listUserActivityMessages(userName)).build();
		}
	}


	/**
	 * Find
	 */
	@GET
	@PermitAll
	@Path("{activityMessageId}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Public.class)
	public ActivityMessage find(@PathParam("activityMessageId") String activityMessageId) {
		return activityMessageService.findActivityMessage(activityMessageId);
	}

	// *************
	// USER ONLY
	// *************
	// XXX
	// NOTE: JsonView should be also applied to GET(none admin resources), once the Json view is enabled on Apron model
	// We haven't used any @JsonView on Apron model, so @JsonView will just ignored

	/**
	 * Create
	 */
	@POST
	@RolesAllowed({Roles.ROLE_ADMIN, Roles.ROLE_TENANT})
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(@NotNull @Valid ActivityMessage activityMessage, @Context SecurityContext context, @Context UriInfo uriInfo) {

		ActivityMessage created = null;
		if (context.isUserInRole(Roles.ROLE_TENANT)) {
			String userName = context.getUserPrincipal().getName();
			created = activityMessageService.createActivityMessage(activityMessage, userName);
		}else{
			created = activityMessageService.createActivityMessage(activityMessage, activityMessage.getVendor().getId());
		}
		URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
		LOG.debug("Created {}", uri);
		return Response.created(uri).build();
	}

	/**
	 * Approve a activity message
	 */
	@POST
	@Path("{activityMessageId}/approve")
	@RolesAllowed(Roles.ROLE_ADMIN)
	public void approveProduct(@PathParam("activityMessageId") String activityMessageId) {
		activityMessageService.approve(activityMessageId, ReviewStatus.APPROVED);
	}


	/**
	 * Disapprove  activity message
	 */
	@POST
	@Path("{activityMessageId}/disapprove")
	@RolesAllowed(Roles.ROLE_ADMIN)
	public void disapproveProduct(@PathParam("activityMessageId") String activityMessageId, @NotNull JsonObject rejectedReason) {
		String reason = getReason(rejectedReason);
		activityMessageService.disapprove(activityMessageId, ReviewStatus.REJECTED, reason);
	}


	/**
	 * Publish activity message
	 */
	@POST
	@Path("{activityMessageId}/publish")
	@JsonView(JsonViews.Admin.class)
	@RolesAllowed({Roles.ROLE_ADMIN, Roles.ROLE_TENANT})
	@Produces(MediaType.APPLICATION_JSON)
	public ActivityMessage publish(@PathParam("activityMessageId") String activityMessageId) {
		return activityMessageService.publish(activityMessageId, true);
	}

	/**
	 * Unpublish activity message
	 */
	@POST
	@Path("{activityMessageId}/unpublish")
	@JsonView(JsonViews.Admin.class)
	@RolesAllowed({Roles.ROLE_ADMIN, Roles.ROLE_TENANT})
	@Produces(MediaType.APPLICATION_JSON)
	public ActivityMessage unpublish(@PathParam("activityMessageId") String activityMessageId) {
		return activityMessageService.publish(activityMessageId, false);
	}

	/**
	 * Update
	 */
	@PUT
	@Path("{activityMessageId}")
	@RolesAllowed({Roles.ROLE_ADMIN, Roles.ROLE_TENANT})
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.User.class)
	public ActivityMessage update(@PathParam("activityMessageId") String activityMessageId, @NotNull @Valid ActivityMessage activityMessage, @Context SecurityContext context) {
		return activityMessageService.updateActivityMessage(activityMessageId, activityMessage);
	}

	/**
	 * Delete
	 */
	@DELETE
	@Path("{activityMessageService}")
	@RolesAllowed({Roles.ROLE_ADMIN, Roles.ROLE_TENANT})
	public void delete(@PathParam("activityMessageId") String activityMessageId) {
		activityMessageService.deleteActivityMessage(activityMessageId);
	}

	/**
	 * Delete all
	 */
	@DELETE
	@RolesAllowed(Roles.ROLE_USER)
	public void deleteAll() {
		activityMessageService.deleteActivityMessages();
	}

}
