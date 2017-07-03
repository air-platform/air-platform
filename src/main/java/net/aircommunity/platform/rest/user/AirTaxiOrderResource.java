package net.aircommunity.platform.rest.user;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.AirTaxiOrder;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.order.AirTaxiOrderService;
import net.aircommunity.platform.service.order.StandardOrderService;

/**
 * AirTaxiOrder RESTful API for ADMIN and USER.
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_USER })
public class AirTaxiOrderResource extends UserOrderResourceSupport<AirTaxiOrder> {
	// private static final Logger LOG = LoggerFactory.getLogger(AirTaxiOrderResource.class);

	@Resource
	private AirTaxiOrderService airTaxiOrderService;

	@Override
	protected StandardOrderService<AirTaxiOrder> getStandardOrderService() {
		return airTaxiOrderService;
	}

	// /**
	// * Create
	// */
	// @POST
	// @Consumes(MediaType.APPLICATION_JSON)
	// @JsonView(JsonViews.User.class)
	// public Response create(@HeaderParam("user-agent") String userAgent, @PathParam("userId") String userId,
	// @NotNull @Valid AirTaxiOrder order, @Context UriInfo uriInfo) {
	// LOG.debug("[{}] Creating order {}", userAgent, order);
	// AirTaxiOrder created = airTaxiOrderService.createAirTaxiOrder(userId, detectOrderChannel(userAgent, order));
	// URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
	// LOG.debug("Created {}", uri);
	// return Response.created(uri).build();
	// }
	//
	// /**
	// * List TODO query
	// */
	// @GET
	// @Produces(MediaType.APPLICATION_JSON)
	// @JsonView(JsonViews.User.class)
	// public Page<AirTaxiOrder> list(@PathParam("userId") String userId, @QueryParam("status") Order.Status status,
	// @QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
	// return airTaxiOrderService.listUserAirTaxiOrders(userId, status, page, pageSize);
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
	// public AirTaxiOrder update(@PathParam("orderId") String orderId, @NotNull @Valid AirTaxiOrder newOrder) {
	// return airTaxiOrderService.updateAirTaxiOrder(orderId, newOrder);
	// }
}
