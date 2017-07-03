package net.aircommunity.platform.rest.user;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.AirTransportOrder;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.order.AirTransportOrderService;
import net.aircommunity.platform.service.order.StandardOrderService;

/**
 * AirTransportOrder RESTful API for ADMIN and USER.
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_USER })
public class AirTransportOrderResource extends UserOrderResourceSupport<AirTransportOrder> {
	// private static final Logger LOG = LoggerFactory.getLogger(AirTransportOrderResource.class);

	@Resource
	private AirTransportOrderService airTransportOrderService;

	@Override
	protected StandardOrderService<AirTransportOrder> getStandardOrderService() {
		return airTransportOrderService;
	}

	// /**
	// * Create
	// */
	// @POST
	// @Consumes(MediaType.APPLICATION_JSON)
	// @JsonView(JsonViews.User.class)
	// public Response create(@HeaderParam("user-agent") String userAgent, @PathParam("userId") String userId,
	// @NotNull @Valid AirTransportOrder order, @Context UriInfo uriInfo) {
	// LOG.debug("[{}] Creating order {}", userAgent, order);
	// AirTransportOrder created = airTransportOrderService.createAirTransportOrder(userId,
	// detectOrderChannel(userAgent, order));
	// URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
	// LOG.debug("Created {}", uri);
	// return Response.created(uri).build();
	// }
	//
	// /**
	// * List TODO query more
	// */
	// @GET
	// @Produces(MediaType.APPLICATION_JSON)
	// @JsonView(JsonViews.User.class)
	// public Page<AirTransportOrder> list(@PathParam("userId") String userId, @QueryParam("status") Order.Status
	// status,
	// @QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
	// return airTransportOrderService.listUserAirTransportOrders(userId, status, page, pageSize);
	// }
	//
	// /**
	// * Update
	// */
	// @PUT
	// @Path("{orderId}")
	// @Consumes(MediaType.APPLICATION_JSON)
	// @Produces(MediaType.APPLICATION_JSON)
	// @JsonView(JsonViews.User.class)
	// public AirTransportOrder update(@PathParam("orderId") String orderId, @NotNull @Valid AirTransportOrder newOrder)
	// {
	// return airTransportOrderService.updateAirTransportOrder(orderId, newOrder);
	// }
}
