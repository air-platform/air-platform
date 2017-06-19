package net.aircommunity.platform.service.internal;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.micro.common.collect.ImmutableCollectors;
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
import net.aircommunity.platform.service.CharterOrderService;
import net.aircommunity.platform.service.FleetService;

/**
 * CharterOrder service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional
public class CharterOrderServiceImpl extends AbstractOrderService<CharterOrder> implements CharterOrderService {
	private static final String CACHE_NAME = "cache.charterorder";

	@Resource
	private CharterOrderRepository charterOrderRepository;

	@Resource
	private FleetCandidateRepository fleetCandidateRepository;

	@Resource
	private FleetService fleetService;

	@Override
	public CharterOrder createCharterOrder(String userId, CharterOrder order) {
		Set<FleetCandidate> candidates = order.getFleetCandidates();
		if (candidates == null || candidates.isEmpty()) {
			return doCreateOrder(userId, order, Order.Status.PUBLISHED);
		}
		return doCreateOrder(userId, order);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public CharterOrder findCharterOrder(String orderId) {
		return doFindOrder(orderId);
	}

	@Override
	public CharterOrder findCharterOrderByOrderNo(String orderNo) {
		return doFindOrderByOrderNo(orderNo);
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public CharterOrder updateCharterOrder(String orderId, CharterOrder newOrder) {
		return doUpdateOrder(orderId, newOrder);
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public CharterOrder updateCharterOrderStatus(String orderId, Order.Status status) {
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
					}
				});
			}
			tgt.setFleetCandidates(fleetCandidates);
		}
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public CharterOrder selectFleetCandidate(String orderId, String fleetCandidateId) {
		CharterOrder charterOrder = findCharterOrder(orderId);
		charterOrder.selectFleetCandidate(fleetCandidateId);
		return safeExecute(() -> {
			return charterOrderRepository.save(charterOrder);
		}, "Select FleetCandidate %s for order %s failed", fleetCandidateId, orderId);
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public CharterOrder offerFleetCandidate(String orderId, String fleetCandidateId, BigDecimal totalAmount) {
		if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new AirException(Codes.ORDER_INVALID_TOTAL_AMOUNT, M.msg(M.ORDER_INVALID_OFFER_TOTAL_AMOUNT));
		}
		CharterOrder charterOrder = findCharterOrder(orderId);
		charterOrder.offerFleetCandidate(fleetCandidateId);
		charterOrder.setTotalPrice(totalAmount);
		CharterOrder charterOrderSaved = safeExecute(() -> {
			return charterOrderRepository.save(charterOrder);
		}, "Offer FleetCandidate %s for order %s failed", fleetCandidateId, orderId);
		eventBus.post(new OrderEvent(OrderEvent.EventType.FLEET_OFFERED, charterOrder));
		return charterOrderSaved;
	}

	@Override
	public Page<CharterOrder> listUserCharterOrders(String userId, Status status, int page, int pageSize) {
		return doListUserOrders(userId, status, page, pageSize);
	}

	@Override
	public Page<CharterOrder> listTenantCharterOrders(String tenantId, Status status, int page, int pageSize) {
		if (Order.Status.DELETED == status) {
			return Page.emptyPage(page, pageSize);
		}
		Page<FleetCandidate> data = null;
		if (status == null) {
			data = Pages.adapt(fleetCandidateRepository.findDistinctOrderByVendorIdAndStatusNot(tenantId,
					FleetCandidate.Status.DELETED, Pages.createPageRequest(page, pageSize)));
		}
		else {
			data = Pages.adapt(fleetCandidateRepository.findDistinctOrderByVendorIdAndOrderStatus(tenantId, status,
					Pages.createPageRequest(page, pageSize)));
		}
		List<CharterOrder> content = data.getContent().stream().map(fleetCandidate -> fleetCandidate.getOrder())
				.distinct().collect(ImmutableCollectors.toList());
		return Pages.createPage(page, pageSize, data.getTotalRecords(), content);
	}

	@Override
	public Page<CharterOrder> listCharterOrders(Status status, int page, int pageSize) {
		return doListAllOrders(status, page, pageSize);
	}

	@CacheEvict(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public void deleteCharterOrder(String orderId) {
		doDeleteOrder(orderId);
	}

	@CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	@Override
	public void deleteCharterOrders(String userId) {
		doDeleteOrders(userId);
	}

	@Override
	protected Code orderNotFoundCode() {
		return Codes.CHARTERORDER_NOT_FOUND;
	}

	@Override
	protected BaseOrderRepository<CharterOrder> getOrderRepository() {
		return charterOrderRepository;
	}

}
