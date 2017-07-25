package net.aircommunity.platform.service.internal.order;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CachePut;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableList;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.AircraftCandidate;
import net.aircommunity.platform.model.domain.CandidateOrder;
import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.model.domain.Order.Status;
import net.aircommunity.platform.model.domain.OrderItemCandidate;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.repository.BaseOrderRepository;
import net.aircommunity.platform.repository.OrderItemCandidateRepository;
import net.aircommunity.platform.service.internal.Pages;
import net.aircommunity.platform.service.order.CandidateOrderService;

/**
 * Abstract Standard order service with order item candidate support.
 * 
 * @author Bin.Zhang
 *
 * @param <C> order candidate type
 * @param <T> order type
 */
abstract class AbstractOrderCandidateService<C extends OrderItemCandidate, T extends CandidateOrder<C>>
		extends AbstractStandardOrderService<T> implements CandidateOrderService<C, T> {

	protected final BaseOrderRepository<T> orderRepository;
	protected final OrderItemCandidateRepository<C> candidateRepository;

	public AbstractOrderCandidateService(BaseOrderRepository<T> orderRepository,
			OrderItemCandidateRepository<C> candidateRepository) {
		this.orderRepository = orderRepository;
		this.candidateRepository = candidateRepository;
	}

	@Override
	protected void doInitialize() {
		setOrderProcessor(new StandardOrderProcessor<T>(configuration, orderRepository));
	}

	protected BaseOrderRepository<T> getOrderRepository() {
		return orderRepository;
	}

	protected OrderItemCandidateRepository<C> getCandidateRepository() {
		return candidateRepository;
	}

	// NOTE: not use default, just override (special case for CandidateOrder)
	@Transactional
	@CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public T updateOrderStatus(String orderId, Order.Status status) {
		if (Order.Status.DELETED == status) {
			// update all candidates as deleted when an order is deleted
			List<C> candidates = candidateRepository.findByOrderId(orderId);
			candidates.stream().forEach(candidate -> candidate.setStatus(OrderItemCandidate.Status.DELETED));
			safeExecute(() -> candidateRepository.save(candidates), "Update order %s status to %s failed", orderId,
					status);
		}
		return doUpdateOrderStatus(orderId, status);
	}

	@Transactional
	@CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public T offerOrderCandidate(String orderId, String candidateId, BigDecimal offeredAmount) {
		ensureTotalAmount(offeredAmount);
		T order = findOrder(orderId);
		Optional<C> offered = order.offerCandidate(candidateId, offeredAmount);
		if (!offered.isPresent()) {
			LOG.error("offer candidate failed, the requested candidateId: {} not found", candidateId);
			throw new AirException(Codes.ORDER_ITEM_CANDIDATE_NOT_FOUND, M.msg(M.ORDER_ITEM_CANDIDATE_NOT_FOUND));
		}
		T orderSaved = safeExecute(() -> orderRepository.save(order), "Offer candidate %s for order %s failed",
				candidateId, orderId);
		onOrderCandidateOffered(orderSaved);
		return orderSaved;
	}

	/**
	 * Template method to be overridden by subclass
	 * 
	 * @param order the order
	 */
	protected void onOrderCandidateOffered(T order) {
	}

	@Transactional
	@CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public T refuseOrderCandidate(String orderId, String candidateId) {
		T order = findOrder(orderId);
		Optional<C> refused = order.refuseCandidate(candidateId);
		if (!refused.isPresent()) {
			LOG.error("refuse candidate failed, the requested candidateId: {} not found", candidateId);
			throw new AirException(Codes.ORDER_ITEM_CANDIDATE_NOT_FOUND, M.msg(M.ORDER_ITEM_CANDIDATE_NOT_FOUND));
		}
		T orderSaved = safeExecute(() -> orderRepository.save(order), "Refuse candidate %s for order %s failed",
				candidateId, orderId);
		onOrderCandidateRefused(orderSaved);
		return orderSaved;
	}

	/**
	 * Template method to be overridden by subclass
	 * 
	 * @param order the order
	 */
	protected void onOrderCandidateRefused(T order) {
	}

	@Transactional
	@CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public T selectOrderCandidate(String orderId, String candidateId) {
		T order = findOrder(orderId);
		Optional<C> selected = order.selectCandidate(candidateId);
		if (!selected.isPresent()) {
			LOG.error("select candidate failed, the requested candidateId: {} not found", candidateId);
			throw new AirException(Codes.ORDER_ITEM_CANDIDATE_NOT_FOUND, M.msg(M.ORDER_ITEM_CANDIDATE_NOT_FOUND));
		}
		BigDecimal totalPrice = selected.get().getOfferedPrice();
		order.setTotalPrice(totalPrice);
		T orderSaved = doUpdateOrderStatus(order, Order.Status.CUSTOMER_CONFIRMED);
		onOrderCandidateSelected(orderSaved);
		return orderSaved;
	}

	/**
	 * Template method to be overridden by subclass
	 * 
	 * @param order the order
	 */
	protected void onOrderCandidateSelected(T order) {
	}

	@Override
	public Function<String, T> getOrderFinder() {
		return orderId -> orderRepository.findOne(orderId);
	}

	@Override
	public Function<T, T> getOrderPersister() {
		return t -> orderRepository.save(t);
	}

	@Override
	public Page<T> listTenantOrders(String tenantId, Status status, int page, int pageSize) {
		if (Order.Status.DELETED == status) {
			return Page.emptyPage(page, pageSize);
		}
		Page<C> data = null;
		if (status == null) {
			data = Pages.adapt(candidateRepository.findDistinctOrderByVendorIdAndStatusNotOrderByOrderCreationDateDesc(
					tenantId, AircraftCandidate.Status.DELETED, Pages.createPageRequest(page, pageSize)));
		}
		else {
			data = Pages.adapt(
					candidateRepository.findDistinctOrderByVendorIdAndOrderStatusOrderByOrderCreationDateDesc(tenantId,
							status, Pages.createPageRequest(page, pageSize)));
		}
		LOG.debug("Before filtering {} candidate data: {}", tenantId, data);
		Map<String, T> result = new LinkedHashMap<>();
		data.getContent().stream().forEach(theCandidate -> {
			// make copy
			T order = doCloneOrder(theCandidate);
			// remove candidate whose owner is NOT current tenant
			// means tenant can only see his own candidate of this order
			Set<C> candidates = order.getCandidates().stream().filter(candidate -> candidate.isOwnedByTenant(tenantId))
					.collect(Collectors.toSet());
			order.setCandidates(candidates);
			result.putIfAbsent(order.getId(), order);
		});
		LOG.debug("Final quick flight order: {}", result);
		return Pages.createPage(data.getPage(), data.getPageSize(), data.getTotalRecords(),
				ImmutableList.copyOf(result.values()));
	}

	protected abstract T doCloneOrder(C candidate);

}
