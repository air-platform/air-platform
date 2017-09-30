package net.aircommunity.platform.rest;

import com.fasterxml.jackson.annotation.JsonView;
import io.micro.annotation.RESTful;
import io.swagger.annotations.Api;
import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
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

@Api
@RESTful
@RolesAllowed({Roles.ROLE_USER})
public class PushNotificationResource {
	private static final Logger LOG = LoggerFactory.getLogger(PushNotificationResource.class);


	@Resource
	private PushNotificationService pushNotificationService;

	/**
	 * Find
	 */
	@GET
	@Path("{pushNotificationId}")
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({Roles.ROLE_USER})
	@JsonView(JsonViews.Public.class)
	public PushNotification find(@PathParam("pushNotificationId") String pushNotificationId) {
		return pushNotificationService.findPushNotification(pushNotificationId);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({Roles.ROLE_USER})
	@JsonView(JsonViews.Public.class)
	public Page<PushNotification> listPushNotifications(@QueryParam("page") @DefaultValue("0") int page,
														@QueryParam("pageSize") @DefaultValue("0") int pageSize, @Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		return pushNotificationService.listUserPushNotifications(accountId, page, pageSize);
	}


	/**
	 * Delete
	 */
	@DELETE
	@Path("{pushNotificationId}")
	@RolesAllowed({Roles.ROLE_USER})
	public void delete(@PathParam("pushNotificationId") String pushNotificationId) {
		pushNotificationService.deletePushNotification(pushNotificationId);
	}


}
