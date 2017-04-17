package net.aircommunity.platform.service.internal;

import java.lang.reflect.ParameterizedType;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.User;
import net.aircommunity.platform.repository.BaseOrderRepository;

/**
 * Abstract Order service support.
 * 
 * @author Bin.Zhang
 */
abstract class AbstractOrderService<T extends Order> extends AbstractServiceSupport {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractOrderService.class);

	protected Class<T> type;

	@PostConstruct
	@SuppressWarnings("unchecked")
	private void init() {
		ParameterizedType pt = ParameterizedType.class.cast(getClass().getGenericSuperclass());
		type = (Class<T>) pt.getActualTypeArguments()[0];
	}

	protected abstract Code orderNotFoundCode();

	protected abstract BaseOrderRepository<T> getOrderRepository();

	// *********************
	// Generic CRUD shared
	// *********************

	protected T createOrder(String userId, T order) {
		User owner = findAccount(userId, User.class);
		T newOrder = null;
		try {
			newOrder = type.newInstance();
		}
		catch (Exception unexpected) {
			LOG.error(String.format("Failed to create instance %s for user %s, cause: ", type, userId,
					unexpected.getMessage()), unexpected);
			throw new AirException(Codes.INTERNAL_ERROR,
					String.format("Failed to create instance %s", type.getSimpleName()));
		}
		newOrder.setCreationDate(new Date());
		newOrder.setStatus(Order.Status.PENDING);
		copyProperties(order, newOrder);
		// set vendor
		newOrder.setOwner(owner);
		try {
			return getOrderRepository().save(newOrder);
		}
		catch (Exception e) {
			LOG.error(String.format("Create %s: %s for user %s failed, cause: ", type.getSimpleName(), userId,
					e.getMessage()), e);
			throw new AirException(Codes.INTERNAL_ERROR,
					String.format("Create %s: %s failed", type.getSimpleName(), order));
		}
	}

	protected T findOrder(String orderId) {
		T order = getOrderRepository().findOne(orderId);
		if (order == null) {
			throw new AirException(orderNotFoundCode(),
					String.format("%s: %s is not found", type.getSimpleName(), orderId));
		}
		return order;
	}

	protected T findOrderByOrderNo(String orderNo) {
		T order = getOrderRepository().findByOrderNo(orderNo);
		if (order == null) {
			throw new AirException(orderNotFoundCode(),
					String.format("%s: NO: %s is not found", type.getSimpleName(), orderNo));
		}
		return order;
	}

	protected T updateOrder(String orderId, T newOrder) {
		T order = findOrder(orderId); // FIXME findOrder is NOT cached?
		copyProperties(newOrder, order);
		try {
			return getOrderRepository().save(order);
		}
		catch (Exception e) {
			LOG.error(String.format("Update %s: %s with %s failed, cause: ", type.getSimpleName(), orderId, newOrder,
					e.getMessage()), e);
			throw new AirException(Codes.INTERNAL_ERROR,
					String.format("Update %s: %s failed", type.getSimpleName(), orderId));
		}
	}

	protected void copyProperties(T src, T tgt) {
	}

	protected T updateOrderStatus(String orderId, Order.Status status) {
		T order = findOrder(orderId);
		order.setStatus(status);
		return getOrderRepository().save(order);
	}

	/**
	 * For USER (Exclude orders in DELETED status)
	 */
	protected Page<T> listUserOrders(String userId, Order.Status status, int page, int pageSize) {
		if (Order.Status.DELETED == status) {
			return Page.emptyPage(page, pageSize);
		}
		if (status == null) {
			return Pages.adapt(getOrderRepository().findByOwnerIdAndStatusNotOrderByCreationDateDesc(userId,
					Order.Status.DELETED, Pages.createPageRequest(page, pageSize)));
		}
		return Pages.adapt(getOrderRepository().findByOwnerIdAndStatusOrderByCreationDateDesc(userId, status,
				Pages.createPageRequest(page, pageSize)));
	}

	/**
	 * For ADMIN for a user (orders in any status)
	 */
	protected Page<T> listAllUserOrders(String userId, Order.Status status, int page, int pageSize) {
		if (status == null) {
			return Pages.adapt(getOrderRepository().findByOwnerIdOrderByCreationDateDesc(userId,
					Pages.createPageRequest(page, pageSize)));
		}
		return Pages.adapt(getOrderRepository().findByOwnerIdAndStatusOrderByCreationDateDesc(userId, status,
				Pages.createPageRequest(page, pageSize)));
	}

	/**
	 * For ADMIN (orders in any status)
	 */
	protected Page<T> listAllOrders(Order.Status status, int page, int pageSize) {
		if (status == null) {
			return Pages.adapt(
					getOrderRepository().findAllByOrderByCreationDateDesc(Pages.createPageRequest(page, pageSize)));
		}
		return Pages.adapt(getOrderRepository().findByStatusOrderByCreationDateDesc(status,
				Pages.createPageRequest(page, pageSize)));
	}

	/**
	 * For ADMIN
	 */
	protected void deleteOrder(String orderId) {
		getOrderRepository().delete(orderId);
	}

	/**
	 * For ADMIN
	 */
	protected void deleteOrders(String userId) {
		getOrderRepository().deleteByOwnerId(userId);
	}
}