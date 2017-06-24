package net.aircommunity.platform.rest.user;

import java.net.URI;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
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
import net.aircommunity.platform.model.domain.JetTravelOrder;
import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.JetTravelOrderService;

/**
 * JetTravelOrder RESTful API.
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_USER })
public class JetTravelOrderResource extends UserBaseOrderResource<JetTravelOrder> {
	private static final Logger LOG = LoggerFactory.getLogger(JetTravelOrderResource.class);

	@Resource
	private JetTravelOrderService jetTravelOrderService;

	/**
	 * Create
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.User.class)
	public Response create(@HeaderParam("user-agent") String userAgent, @PathParam("userId") String userId,
			@NotNull @Valid JetTravelOrder order, @Context UriInfo uriInfo) {
		LOG.debug("[{}] Creating order {}", userAgent, order);
		JetTravelOrder created = jetTravelOrderService.createJetTravelOrder(userId,
				detectOrderChannel(userAgent, order));
		URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
		LOG.debug("Created {}", uri);
		return Response.created(uri).build();
	}

	/**
	 * List
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.User.class)
	public Page<JetTravelOrder> list(@PathParam("userId") String userId, @QueryParam("status") Order.Status status,
			@QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		return jetTravelOrderService.listUserJetTravelOrders(userId, status, page, pageSize);
	}

	/**
	 * Update
	 */
	@PUT
	@Path("{orderId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.User.class)
	public JetTravelOrder update(@PathParam("orderId") String orderId, @NotNull @Valid JetTravelOrder newOrder) {
		return jetTravelOrderService.updateJetTravelOrder(orderId, newOrder);
	}
}
