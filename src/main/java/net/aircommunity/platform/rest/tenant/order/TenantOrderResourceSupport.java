package net.aircommunity.platform.rest.tenant.order;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonView;

import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.rest.ManagedOrderResourceSupport;

/**
 * Base Tenant order RESTful API for ADMIN and TENANT
 * 
 * @author Bin.Zhang
 */
public abstract class TenantOrderResourceSupport<T extends Order> extends ManagedOrderResourceSupport<T> {
	protected static final Logger LOG = LoggerFactory.getLogger(TenantOrderResourceSupport.class);

	/**
	 * Search order
	 */
	@GET
	@Path("search")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	public List<T> search(@PathParam("tenantId") String tenantId, @NotNull @QueryParam("orderNo") String orderNo) {
		return getOrderService().searchTenantOrder(tenantId, orderNo);
	}

	/**
	 * Find order
	 */
	@GET
	@Path("{orderId}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	public T find(@PathParam("orderId") String orderId) {
		T order = getOrderService().findOrder(orderId);
		// NOTE: it can be still returns order in DELETED status, because of ADMIN find order may cached
		return ensureOrderVisible(order);
	}
}
