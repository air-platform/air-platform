package net.aircommunity.platform.service.internal.order;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micro.common.Strings;
import net.aircommunity.platform.Configuration;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.model.domain.Order.Status;
import net.aircommunity.platform.model.domain.Product.Type;
import net.aircommunity.platform.repository.BaseOrderRepository;
import net.aircommunity.platform.service.internal.Pages;
import net.aircommunity.platform.service.spi.OrderProcessor;

/**
 * Standard order processor based on BaseOrderRepository.
 * 
 * @author Bin.Zhang
 * @param <T> the order type
 */
public class StandardOrderProcessor<T extends Order> implements OrderProcessor<T> {
	private static final Logger LOG = LoggerFactory.getLogger(StandardOrderProcessor.class);

	private final Configuration config;
	private final BaseOrderRepository<T> orderRepository;

	public StandardOrderProcessor(Configuration config, BaseOrderRepository<T> orderRepository) {
		this.config = Objects.requireNonNull(config, "config cannot be null");
		this.orderRepository = Objects.requireNonNull(orderRepository, "orderRepository cannot be null");
	}

	@Override
	public T findByOrderId(String orderId) {
		return orderRepository.findOne(orderId);
	}

	@Override
	public List<T> searchOrder(String orderNo) {
		if (Strings.isBlank(orderNo)) {
			return Collections.emptyList();
		}
		return orderRepository.findVisibleByFuzzyOrderNo(orderNo);
	}

	@Override
	public List<T> findOrders(List<String> orderIds) {
		return orderRepository.findAll(orderIds);
	}

	@Override
	public T findByOrderNo(String orderNo) {
		return orderRepository.findByOrderNo(orderNo);
	}

	@Override
	public T saveOrder(T order) {
		return orderRepository.save(order);
	}

	@Override
	public Page<T> listUserOrders(String userId, Order.Status status, int page, int pageSize) {
		if (Order.Status.DELETED == status) {
			return Page.emptyPage(page, pageSize);
		}
		if (status == null) {
			return Pages.adapt(orderRepository.findByOwnerIdAndStatusInOrderByCreationDateDesc(userId,
					Order.Status.VISIBLE_STATUSES, Pages.createPageRequest(page, pageSize)));
		}
		return Pages.adapt(orderRepository.findByOwnerIdAndStatusOrderByCreationDateDesc(userId, status,
				Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<T> listUserOrders(String userId, Set<Status> statuses, int page, int pageSize) {
		Set<Order.Status> incudedStatuses = new HashSet<>(statuses);
		incudedStatuses.remove(Order.Status.DELETED);
		return Pages.adapt(orderRepository.findByOwnerIdAndStatusInOrderByCreationDateDesc(userId, incudedStatuses,
				Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<T> listUserOrdersOfRecentDays(String userId, int days, int page, int pageSize) {
		if (days < 0) {
			days = 0;
		}
		if (days > config.getOrderQueryDayMax()) {
			days = config.getOrderQueryDayMax();
		}
		Date creationDate = LocalDateTime.now().minusDays(days).toDate();
		LOG.debug("List recent {} days({}) orders for user: {}", days, creationDate, userId);
		return Pages.adapt(
				orderRepository.findByOwnerIdAndStatusInAndCreationDateGreaterThanEqualOrderByCreationDateDesc(userId,
						Order.Status.VISIBLE_STATUSES, creationDate, Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<T> listAllUserOrders(String userId, Status status, Type type, int page, int pageSize) {
		if (status != null) {
			if (type != null) {
				return Pages.adapt(orderRepository.findByOwnerIdAndStatusAndTypeOrderByCreationDateDesc(userId, status,
						type, Pages.createPageRequest(page, pageSize)));
			}
			return Pages.adapt(orderRepository.findByOwnerIdAndStatusOrderByCreationDateDesc(userId, status,
					Pages.createPageRequest(page, pageSize)));
		}
		if (type != null) {
			return Pages.adapt(orderRepository.findByOwnerIdAndTypeOrderByCreationDateDesc(userId, type,
					Pages.createPageRequest(page, pageSize)));
		}
		return Pages.adapt(
				orderRepository.findByOwnerIdOrderByCreationDateDesc(userId, Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<T> listAllOrders(Order.Status status, Type type, int page, int pageSize) {
		if (status != null) {
			if (type != null) {
				return Pages.adapt(orderRepository.findByStatusAndTypeOrderByCreationDateDesc(status, type,
						Pages.createPageRequest(page, pageSize)));
			}
			return Pages.adapt(orderRepository.findByStatusOrderByCreationDateDesc(status,
					Pages.createPageRequest(page, pageSize)));
		}
		if (type != null) {
			return Pages.adapt(
					orderRepository.findByTypeOrderByCreationDateDesc(type, Pages.createPageRequest(page, pageSize)));
		}
		return Pages.adapt(orderRepository.findAllByOrderByCreationDateDesc(Pages.createPageRequest(page, pageSize)));
	}

}
