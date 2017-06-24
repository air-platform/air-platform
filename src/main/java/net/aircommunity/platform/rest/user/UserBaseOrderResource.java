package net.aircommunity.platform.rest.user;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.annotation.JsonView;

import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.rest.BaseResourceSupport;
import net.aircommunity.platform.service.CharterOrderService;
import net.aircommunity.platform.service.CommonOrderService;

/**
 * Base User order RESTful API. <b>all permission</b> for ADMIN/USER
 * 
 * @author Bin.Zhang
 */
public abstract class UserBaseOrderResource<T extends Order> extends BaseResourceSupport {

	@Resource
	private CommonOrderService commonOrderService;

	@Resource
	private CharterOrderService charterOrderService;

	/**
	 * Detect order channel from UA.
	 * 
	 * @param userAgent UA
	 * @return
	 * @return the order channel
	 */
	protected <ORDER extends Order> ORDER detectOrderChannel(String userAgent, ORDER order) {
		// TODO parse userAgent for channel
		order.setChannel(userAgent);
		return order;
	}

	/**
	 * Find order
	 */
	@GET
	@Path("{orderId}")
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	@JsonView(JsonViews.User.class)
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_USER })
	public T find(@PathParam("orderId") String orderId) {
		return (T) commonOrderService.findOrder(orderId);
	}

	// *****************
	// ADMIN & USER
	// *****************

	/**
	 * Select a fleet (Only allow one vendor to be selected)
	 */
	@POST
	@Path("{orderId}/fleet/select")
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_USER })
	public void selectFleet(@PathParam("orderId") String orderId,
			@NotNull @QueryParam("candidate") String fleetCandidateId) {
		charterOrderService.selectFleetCandidate(orderId, fleetCandidateId);
	}

	/**
	 * Cancel order
	 */
	@POST
	@Path("{orderId}/cancel")
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_USER })
	public void cancel(@PathParam("orderId") String orderId, JsonObject request) {
		String reason = getCancelReason(request);
		commonOrderService.cancelOrder(orderId, reason);
	}

	/**
	 * Delete (mark order as DELETED)
	 */
	@DELETE
	@Path("{orderId}")
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_USER })
	public void delete(@PathParam("orderId") String orderId) {
		commonOrderService.updateOrderStatus(orderId, Order.Status.DELETED);
	}

}
