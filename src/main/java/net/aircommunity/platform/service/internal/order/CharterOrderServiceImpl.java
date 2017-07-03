package net.aircommunity.platform.service.internal.order;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableList;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.OrderEvent;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.CharterOrder;
import net.aircommunity.platform.model.domain.Fleet;
import net.aircommunity.platform.model.domain.FleetCandidate;
import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.model.domain.Order.Status;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.repository.BaseOrderRepository;
import net.aircommunity.platform.repository.CharterOrderRepository;
import net.aircommunity.platform.repository.FleetCandidateRepository;
import net.aircommunity.platform.service.internal.Pages;
import net.aircommunity.platform.service.order.CharterOrderService;
import net.aircommunity.platform.service.product.FleetService;

/**
 * CharterOrder service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional(readOnly = true)
public class CharterOrderServiceImpl extends AbstractStandardOrderService<CharterOrder> implements CharterOrderService {
	private static final Logger LOG = LoggerFactory.getLogger(CharterOrderServiceImpl.class);

	// TODO REMOVE
	// private static final String CACHE_NAME = "cache.charterorder";

	@Resource
	private CharterOrderRepository charterOrderRepository;

	@Resource
	private FleetCandidateRepository fleetCandidateRepository;

	@Resource
	private FleetService fleetService;

	// NOTE: not use default, just override (special case for CharterOrder)
	@Transactional
	@Override
	public CharterOrder createOrder(String userId, CharterOrder order) {
		Set<FleetCandidate> candidates = order.getFleetCandidates();
		if (candidates == null || candidates.isEmpty()) {
			return doCreateOrder(userId, order, Order.Status.PUBLISHED);
		}
		return doCreateOrder(userId, order);
	}

	// NOTE: not use default, just override (special case for CharterOrder)
	@Transactional
	@CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public CharterOrder updateOrderStatus(String orderId, Order.Status status) {
		if (Order.Status.DELETED == status) {
			// update all fleetCandidates as deleted when an order is deleted
			List<FleetCandidate> fleetCandidates = fleetCandidateRepository.findByOrderId(orderId);
			fleetCandidates.stream().forEach(fleetCandidate -> {
				fleetCandidate.setStatus(FleetCandidate.Status.DELETED);
			});
			safeExecute(() -> {
				fleetCandidateRepository.save(fleetCandidates);
			}, "updateCharterOrder %s status to %s failed", orderId, status);
		}
		return doUpdateOrderStatus(orderId, status);
	}

	@Override
	protected void copyProperties(CharterOrder src, CharterOrder tgt) {
		tgt.setFlightLegs(src.getFlightLegs());
		Set<FleetCandidate> fleetCandidates = src.getFleetCandidates();
		if (fleetCandidates != null) {
			if (fleetCandidates.size() == 1) {
				// make default as selected if single
				FleetCandidate singleCandidate = fleetCandidates.iterator().next();
				Fleet singleFleet = fleetService.findFleet(singleCandidate.getFleet().getId());
				singleCandidate.setStatus(FleetCandidate.Status.SELECTED);
				singleCandidate.setFleet(singleFleet);
			}
			else {
				fleetCandidates.stream().forEach(fleetCandidate -> {
					Fleet fleet = fleetCandidate.getFleet();
					if (fleet != null) {
						fleet = fleetService.findFleet(fleet.getId());
						fleetCandidate.setFleet(fleet);
						// force CANDIDATE if multiple selection
						fleetCandidate.setStatus(FleetCandidate.Status.CANDIDATE);
					}
				});
			}
			tgt.setFleetCandidates(fleetCandidates);
		}
	}

	// XXX
	// @CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	// @Override
	// public CharterOrder updateCharterOrderTotalAmount(String orderId, BigDecimal totalAmount) {
	// return doUpdateOrderTotalAmount(orderId, totalAmount);
	// }

	@Transactional
	@CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public CharterOrder selectFleetCandidate(String orderId, String fleetCandidateId) {
		CharterOrder charterOrder = findCharterOrder(orderId);
		Optional<FleetCandidate> selected = charterOrder.selectFleetCandidate(fleetCandidateId);
		if (!selected.isPresent()) {
			LOG.error("selectFleetCandidate failed, the requested fleetCandidateId: {} not found", fleetCandidateId);
			throw new AirException(Codes.FLEET_CANDIDATE_NOT_FOUND, M.msg(M.FLEET_CANDIDATE_NOT_FOUND));
		}
		BigDecimal totalPrice = selected.get().getOfferedPrice();
		charterOrder.setTotalPrice(totalPrice);
		return doUpdateOrderStatus(charterOrder, Order.Status.CUSTOMER_CONFIRMED);
	}

	@Transactional
	@CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public CharterOrder offerFleetCandidate(String orderId, String fleetCandidateId, BigDecimal offeredAmount) {
		ensureTotalAmount(offeredAmount);
		CharterOrder charterOrder = findCharterOrder(orderId);
		Optional<FleetCandidate> offered = charterOrder.offerFleetCandidate(fleetCandidateId, offeredAmount);
		if (!offered.isPresent()) {
			LOG.error("offerFleetCandidate failed, the requested fleetCandidateId: {} not found", fleetCandidateId);
			throw new AirException(Codes.FLEET_CANDIDATE_NOT_FOUND, M.msg(M.FLEET_CANDIDATE_NOT_FOUND));
		}
		CharterOrder charterOrderSaved = safeExecute(() -> {
			return charterOrderRepository.save(charterOrder);
		}, "Offer FleetCandidate %s for order %s failed", fleetCandidateId, orderId);
		eventBus.post(new OrderEvent(OrderEvent.EventType.FLEET_OFFERED, charterOrder));
		return charterOrderSaved;
	}

	@Transactional
	@CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public CharterOrder refuseFleetCandidate(String orderId, String fleetCandidateId) {
		CharterOrder charterOrder = findCharterOrder(orderId);
		Optional<FleetCandidate> refused = charterOrder.refuseFleetCandidate(fleetCandidateId);
		if (!refused.isPresent()) {
			LOG.error("refuseFleetCandidate failed, the requested fleetCandidateId: {} not found", fleetCandidateId);
			throw new AirException(Codes.FLEET_CANDIDATE_NOT_FOUND, M.msg(M.FLEET_CANDIDATE_NOT_FOUND));
		}
		CharterOrder charterOrderSaved = safeExecute(() -> {
			return charterOrderRepository.save(charterOrder);
		}, "Refuse FleetCandidate %s for order %s failed", fleetCandidateId, orderId);
		eventBus.post(new OrderEvent(OrderEvent.EventType.FLEET_OFFERED, charterOrder));
		return charterOrderSaved;
	}

	// NOTE: not use default, just override (special case for CharterOrder)
	@Override
	public Page<CharterOrder> listTenantOrders(String tenantId, Status status, int page, int pageSize) {
		if (Order.Status.DELETED == status) {
			return Page.emptyPage(page, pageSize);
		}
		Page<FleetCandidate> data = null;
		if (status == null) {
			data = Pages
					.adapt(fleetCandidateRepository.findDistinctOrderByVendorIdAndStatusNotOrderByOrderCreationDateDesc(
							tenantId, FleetCandidate.Status.DELETED, Pages.createPageRequest(page, pageSize)));
		}
		else {
			data = Pages.adapt(
					fleetCandidateRepository.findDistinctOrderByVendorIdAndOrderStatusOrderByOrderCreationDateDesc(
							tenantId, status, Pages.createPageRequest(page, pageSize)));
		}
		LOG.debug("Before filtering {} fleet candidate data: {}", tenantId, data);
		Map<String, CharterOrder> result = new LinkedHashMap<>();
		data.getContent().stream().forEach(fleetCandidate -> {
			// make copy
			CharterOrder order = fleetCandidate.getOrder().clone();
			// remove FleetCandidate whose owner is NOT current tenant
			// means tenant can only see his own FleetCandidate of this order
			Set<FleetCandidate> candidates = order.getFleetCandidates().stream()
					.filter(candidate -> candidate.isOwnedByTenant(tenantId)).collect(Collectors.toSet());
			order.setFleetCandidates(candidates);
			result.putIfAbsent(order.getId(), order);
		});
		LOG.debug("Final charter order: {}", result);
		return Pages.createPage(page, pageSize, data.getTotalRecords(), ImmutableList.copyOf(result.values()));
	}

	@Override
	protected Code orderNotFoundCode() {
		return Codes.CHARTERORDER_NOT_FOUND;
	}

	@Override
	protected BaseOrderRepository<CharterOrder> getOrderRepository() {
		return charterOrderRepository;
	}

	// @Cacheable(cacheNames = CACHE_NAME)
	// @Override
	// public CharterOrder findCharterOrder(String orderId) {
	// return doFindOrder(orderId);
	// }
	//
	// @Override
	// public CharterOrder findCharterOrderByOrderNo(String orderNo) {
	// return doFindOrderByOrderNo(orderNo);
	// }

	// @CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	// @Override
	// public CharterOrder updateCharterOrder(String orderId, CharterOrder newOrder) {
	// return doUpdateOrder(orderId, newOrder);
	// }

	// @Override
	// public Page<CharterOrder> listUserCharterOrders(String userId, Status status, int page, int pageSize) {
	// return doListUserOrders(userId, status, page, pageSize);
	// }

	// @Override
	// public Page<CharterOrder> listCharterOrders(Status status, int page, int pageSize) {
	// return doListAllOrders(status, page, pageSize);
	// }
	//
	// @CacheEvict(cacheNames = CACHE_NAME, key = "#orderId")
	// @Override
	// public void deleteCharterOrder(String orderId) {
	// doDeleteOrder(orderId);
	// }
	//
	// @CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	// @Override
	// public void deleteCharterOrders(String userId) {
	// doDeleteOrders(userId);
	// }

}
