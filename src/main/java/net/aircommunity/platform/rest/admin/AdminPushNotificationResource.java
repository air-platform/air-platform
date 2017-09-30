package net.aircommunity.platform.rest.admin;

import com.fasterxml.jackson.annotation.JsonView;
import io.micro.annotation.RESTful;
import java.net.URI;
import javax.annotation.Resource;
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
import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.PushNotification;
import net.aircommunity.platform.service.common.PushNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PushNotification RESTful API for ADMIN
 *
 * @author Xiangwen.Kong
 */

@RESTful
@RolesAllowed({Roles.ROLE_ADMIN})
public class AdminPushNotificationResource {
	private static final Logger LOG = LoggerFactory.getLogger(AdminPushNotificationResource.class);


	@Resource
	private PushNotificationService pushNotificationService;

	/**
	 * send new message
	 */
	@POST
	@Path("{pushNotificationId}/send")
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({Roles.ROLE_ADMIN})
	public Response sendMessage(@PathParam("pushNotificationId") String pushNotificationId) {
		LOG.debug("Get notification {}", pushNotificationId);
		//pushNotificationService.
		PushNotification pf = pushNotificationService.sendNotification(pushNotificationId);
		return Response.ok(pf).build();
	}


	/**
	 * Create
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@RolesAllowed({Roles.ROLE_ADMIN})
	@JsonView(JsonViews.Admin.class)
	public Response create(@QueryParam("user") String userId, @NotNull @Valid PushNotification pushNotification,
						   @Context UriInfo uriInfo) {
		PushNotification created = pushNotificationService.createPushNotification(pushNotification, userId);
		URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
		LOG.debug("Created {}", uri);
		return Response.created(uri).build();
	}

	/**
	 * Find
	 */
	@GET
	@Path("{pushNotificationId}")
	@Produces(MediaType.APPLICATION_JSON)
	public PushNotification find(@PathParam("pushNotificationId") String pushNotificationId) {
		return pushNotificationService.findPushNotification(pushNotificationId);
	}

	/**
	 * List
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Page<PushNotification> list(@QueryParam("page") @DefaultValue("0") int page,
									   @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		return pushNotificationService.listPushNotifications(page, pageSize);
	}

	/**
	 * Update
	 */
	@PUT
	@Path("{pushNotificationId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({Roles.ROLE_ADMIN})
	public PushNotification update(@PathParam("pushNotificationId") String pushNotificationId, @NotNull @Valid PushNotification pushNotification) {
		return pushNotificationService.updatePushNotification(pushNotificationId, pushNotification);
	}

	/**
	 * Delete
	 */
	@DELETE
	@Path("{pushNotificationId}")
	@RolesAllowed({Roles.ROLE_ADMIN})
	public void delete(@PathParam("pushNotificationId") String pushNotificationId) {
		pushNotificationService.deletePushNotification(pushNotificationId);
	}

	/**
	 * Delete all
	 */
	@DELETE
	@RolesAllowed({Roles.ROLE_ADMIN})
	public void deleteAll() {
		pushNotificationService.deletePushNotifications();
	}

}
