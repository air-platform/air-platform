package net.aircommunity.platform.service.internal.order;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;

import io.micro.common.Strings;
import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.Configuration;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.model.domain.Order.Status;
import net.aircommunity.platform.model.domain.OrderRef;
import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.model.domain.Product.Type;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.repository.OrderRefRepository;
import net.aircommunity.platform.service.internal.Pages;
import net.aircommunity.platform.service.order.StandardOrderService;
import net.aircommunity.platform.service.spi.OrderProcessor;

/**
 * Common order processor without bound to a concrete order type.
 * 
 * @author Bin.Zhang
 */
public class CommonOrderProcessor implements OrderProcessor<Order> {
	private static final Logger LOG = LoggerFactory.getLogger(CommonOrderServiceImpl.class);

	// IMPORTANT, visibleOrderOnly=false, is considered as ADMIN MODE
	private final boolean visibleOrderOnly;
	private final Configuration configuration;
	private final OrderRefRepository orderRefRepository;
	private final Map<Product.Type, StandardOrderService<Order>> orderServiceRegistry;

	public CommonOrderProcessor(boolean visibleOrderOnly, Configuration config, OrderRefRepository repository,
			Map<Type, StandardOrderService<Order>> registry) {
		this.visibleOrderOnly = visibleOrderOnly;
		configuration = Objects.requireNonNull(config, "config cannot be null");
		orderServiceRegistry = Objects.requireNonNull(registry, "orderServiceRegistry cannot be null");
		orderRefRepository = Objects.requireNonNull(repository, "orderRefRepository cannot be null");
	}

	@Override
	public Order findByOrderId(String orderId) {
		OrderRef ref = orderRefRepository.findOne(orderId);
		if (ref == null) {
			LOG.warn("OrderRef orderId: {} is not found", orderId);
			throw new AirException(Codes.ORDER_NOT_FOUND, M.msg(M.ORDER_NOT_FOUND));
		}
		return findOrderByRef(ref);
	}

	@Override
	public List<Order> searchOrder(String orderNo) {
		List<OrderRef> refs = doSearchOrderRef(orderNo);
		return transform(refs);
	}

	@Override
	public List<Order> searchUserOrder(String userId, String orderNo) {
		List<OrderRef> refs = doSearchOrderRef(orderNo);
		// filter ref before lookup
		refs = refs.stream().filter(ref -> ref.getOwnerId().equals(userId)).collect(Collectors.toList());
		return transform(refs);
	}

	@Override
	public List<Order> searchTenantOrder(String tenantId, String orderNo) {
		List<OrderRef> refs = doSearchOrderRef(orderNo);
		// filter ref before lookup
		refs = refs.stream().filter(order -> order.getVendorId().equals(tenantId)).collect(Collectors.toList());
		return transform(refs);
	}

	private List<OrderRef> doSearchOrderRef(String orderNo) {
		if (Strings.isBlank(orderNo)) {
			return Collections.emptyList();
		}
		return orderRefRepository.findByFuzzyOrderNo(visibleOrderOnly, orderNo,
				configuration.getOrderSearchResultMax());
	}

	@Override
	public List<Order> findOrders(List<String> orderIds) {
		List<OrderRef> refs = doFindOrderRefs(orderIds);
		if (visibleOrderOnly) {
			// return non-deleted order only for non-admin
			refs = refs.stream().filter(ref -> ref.getStatus() != Order.Status.DELETED).collect(Collectors.toList());
		}
		return transform(refs);
	}

	@Override
	public List<Order> findUserOrders(String userId, List<String> orderIds) {
		List<OrderRef> refs = doFindOrderRefs(orderIds);
		if (visibleOrderOnly) {
			// return non-deleted order only for non-admin
			refs = refs.stream().filter(ref -> ref.getStatus() != Order.Status.DELETED)
					.filter(ref -> ref.getOwnerId().equals(userId)).collect(Collectors.toList());
		}
		else {
			refs = refs.stream().filter(ref -> ref.getOwnerId().equals(userId)).collect(Collectors.toList());
		}
		return transform(refs);
	}

	@Override
	public List<Order> findTenantOrders(String tenantId, List<String> orderIds) {
		List<OrderRef> refs = doFindOrderRefs(orderIds);
		if (visibleOrderOnly) {
			// return non-deleted order only for non-admin
			refs = refs.stream().filter(ref -> ref.getStatus() != Order.Status.DELETED)
					.filter(ref -> ref.getVendorId().equals(tenantId)).collect(Collectors.toList());
		}
		else {
			refs = refs.stream().filter(ref -> ref.getVendorId().equals(tenantId)).collect(Collectors.toList());
		}
		return transform(refs);
	}

	private List<OrderRef> doFindOrderRefs(List<String> orderIds) {
		if (orderIds == null || orderIds.isEmpty()) {
			return Collections.emptyList();
		}
		return orderRefRepository.findAll(orderIds);
	}

	@Override
	public Order findByOrderNo(String orderNo) {
		OrderRef ref = orderRefRepository.findByOrderNo(orderNo);
		if (ref == null) {
			LOG.warn("OrderRef orderNo: {} is not found", orderNo);
			throw new AirException(Codes.ORDER_NOT_FOUND, M.msg(M.ORDER_NOT_FOUND));
		}
		return findOrderByRef(ref);
	}

	private Order findOrderByRef(OrderRef ref) {
		StandardOrderService<Order> service = getOrderService(ref.getType());
		Order order = service.getOrderFinder().apply(ref.getOrderId());
		if (order == null) {
			throw new AirException(Codes.ORDER_NOT_FOUND, M.msg(M.ORDER_NOT_FOUND));
		}
		// NOTE: DELETED status considered as not found for non-admin
		if (order.getStatus() == Order.Status.DELETED && visibleOrderOnly) {
			throw new AirException(Codes.ORDER_NOT_FOUND, M.msg(M.ORDER_NOT_FOUND));
		}
		return order;
	}

	private StandardOrderService<Order> getOrderService(Type type) {
		StandardOrderService<Order> service = orderServiceRegistry.get(type);
		if (service == null) {
			LOG.error("No StandardOrderService found for order type: {}", type);
			throw new AirException(Codes.SERVICE_UNAVAILABLE, M.msg(M.SERVICE_UNAVAILABLE));
		}
		return service;
	}

	@Override
	public Order saveOrder(Order order) {
		StandardOrderService<Order> service = getOrderService(order.getType());
		return service.getOrderPersister().apply(order);
	}

	@Override
	public Page<Order> listUserOrders(String userId, Status status, int page, int pageSize) {
		if (Order.Status.DELETED == status) {
			return Page.emptyPage(page, pageSize);
		}
		Page<OrderRef> refs = Pages.createEmptyPage(page, pageSize);
		if (status == null) {
			refs = Pages.adapt(orderRefRepository.findByOwnerIdAndStatusInOrderByCreationDateDesc(userId,
					Order.Status.VISIBLE_STATUSES, Pages.createPageRequest(page, pageSize)));
		}
		else {
			refs = Pages.adapt(orderRefRepository.findByOwnerIdAndStatusOrderByCreationDateDesc(userId, status,
					Pages.createPageRequest(page, pageSize)));
		}
		return transform(refs);
	}

	@Override
	public Page<Order> listUserOrders(String userId, Set<Status> statuses, int page, int pageSize) {
		Set<Order.Status> incudedStatuses = new HashSet<>(statuses);
		incudedStatuses.remove(Order.Status.DELETED);
		Page<OrderRef> refs = Pages.adapt(orderRefRepository.findByOwnerIdAndStatusInOrderByCreationDateDesc(userId,
				incudedStatuses, Pages.createPageRequest(page, pageSize)));
		return transform(refs);
	}

	@Override
	public Page<Order> listUserOrdersOfRecentDays(String userId, int days, int page, int pageSize) {
		if (days < 0) {
			days = 0;
		}
		if (days > configuration.getOrderQueryDayMax()) {
			days = configuration.getOrderQueryDayMax();
		}
		Date creationDate = LocalDateTime.now().minusDays(days).toDate();
		LOG.debug("List recent {} days({}) orders for user: {}", days, creationDate, userId);
		Page<OrderRef> refs = Pages.adapt(
				orderRefRepository.findByOwnerIdAndStatusInAndCreationDateGreaterThanEqualOrderByCreationDateDesc(
						userId, Order.Status.VISIBLE_STATUSES, creationDate, Pages.createPageRequest(page, pageSize)));
		return transform(refs);
	}

	@Override
	public Page<Order> listAllUserOrders(String userId, Status status, Type type, int page, int pageSize) {
		Page<OrderRef> refs = Pages.adapt(
				orderRefRepository.findWithConditions(userId, status, type, Pages.createPageRequest(page, pageSize)));
		return transform(refs);
	}

	@Override
	public Page<Order> listAllOrders(Status status, Type type, int page, int pageSize) {
		Page<OrderRef> refs = Pages
				.adapt(orderRefRepository.findWithConditions(status, type, Pages.createPageRequest(page, pageSize)));
		return transform(refs);
	}

	private Page<Order> transform(Page<OrderRef> refs) {
		// 1) Retrieve orderRefs
		List<OrderRef> refList = refs.getContent();
		List<Order> content = transform(refList);
		return Pages.createPage(refs.getPage(), refs.getPageSize(), refs.getTotalRecords(), content);
	}

	private List<Order> transform(List<OrderRef> refs) {
		if (refs.isEmpty()) {
			return Collections.emptyList();
		}
		// 2) Find orders with corresponding orderService
		ImmutableList.Builder<Order> builder = ImmutableList.builder();
		// group by type and find orders with the order IDs in batch with the corresponding orderService
		Map<Type, List<OrderRef>> grouped = refs.stream().collect(Collectors.groupingBy(ref -> ref.getType()));
		// fetch order from DB in parallel
		grouped.entrySet().parallelStream().forEach(entry -> {
			StandardOrderService<Order> service = getOrderService(entry.getKey());
			List<String> orderIds = entry.getValue().stream().map(OrderRef::getOrderId).collect(Collectors.toList());
			LOG.debug("Find orders for ids: {} with type: {}", orderIds, entry.getKey());
			List<Order> orders = service.findOrders(orderIds);
			builder.addAll(orders);
		});
		List<Order> mergedOrders = builder.build();
		LOG.debug("Found {} orders", mergedOrders.size());

		// 3) perform explicit ordering defined by order refs
		List<String> orderRefIds = refs.stream().map(OrderRef::getOrderId).collect(Collectors.toList());
		LOG.debug("Perform explicit ordering for order ids: {}", orderRefIds);
		Ordering<Order> ordering = Ordering.explicit(orderRefIds).onResultOf(Order::getId);
		return ordering.sortedCopy(mergedOrders);
	}
}
