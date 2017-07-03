package net.aircommunity.platform.rest.user;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.JetTravelOrder;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.order.JetTravelOrderService;
import net.aircommunity.platform.service.order.StandardOrderService;

/**
 * JetTravelOrder RESTful API for ADMIN and USER.
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_USER })
public class JetTravelOrderResource extends UserOrderResourceSupport<JetTravelOrder> {
	// private static final Logger LOG = LoggerFactory.getLogger(JetTravelOrderResource.class);

	@Resource
	private JetTravelOrderService jetTravelOrderService;

	@Override
	protected StandardOrderService<JetTravelOrder> getStandardOrderService() {
		return jetTravelOrderService;
	}

	// /**
	// * Create
	// */
	// @POST
	// @Consumes(MediaType.APPLICATION_JSON)
	// @JsonView(JsonViews.User.class)
	// public Response create(@HeaderParam("user-agent") String userAgent, @PathParam("userId") String userId,
	// @NotNull @Valid JetTravelOrder order, @Context UriInfo uriInfo) {
	// LOG.debug("[{}] Creating order {}", userAgent, order);
	// JetTravelOrder created = jetTravelOrderService.createJetTravelOrder(userId,
	// detectOrderChannel(userAgent, order));
	// URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
	// LOG.debug("Created {}", uri);
	// return Response.created(uri).build();
	// }
	//
	// /**
	// * List
	// */
	// @GET
	// @Produces(MediaType.APPLICATION_JSON)
	// @JsonView(JsonViews.User.class)
	// public Page<JetTravelOrder> list(@PathParam("userId") String userId, @QueryParam("status") Order.Status status,
	// @QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
	// return jetTravelOrderService.listUserJetTravelOrders(userId, status, page, pageSize);
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
	// public JetTravelOrder update(@PathParam("orderId") String orderId, @NotNull @Valid JetTravelOrder newOrder) {
	// return jetTravelOrderService.updateJetTravelOrder(orderId, newOrder);
	// }
}
