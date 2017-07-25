package net.aircommunity.platform.service.internal.order;

import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.OrderEvent;
import net.aircommunity.platform.model.domain.CharterOrder;
import net.aircommunity.platform.model.domain.Fleet;
import net.aircommunity.platform.model.domain.FleetCandidate;
import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.repository.CharterOrderRepository;
import net.aircommunity.platform.repository.FleetCandidateRepository;
import net.aircommunity.platform.service.order.CharterOrderService;
import net.aircommunity.platform.service.order.annotation.ManagedOrderService;
import net.aircommunity.platform.service.product.FleetService;

/**
 * CharterOrder service implementation.
 * 
 * @author Bin.Zhang
 */
@ManagedOrderService(Product.Type.FLEET)
@Transactional(readOnly = true)
public class CharterOrderServiceImpl extends AbstractOrderCandidateService<FleetCandidate, CharterOrder>
		implements CharterOrderService {

	@Resource
	private FleetService fleetService;

	@Autowired
	public CharterOrderServiceImpl(CharterOrderRepository orderRepository,
			FleetCandidateRepository candidateRepository) {
		super(orderRepository, candidateRepository);
	}

	@Override
	protected void copyProperties(CharterOrder src, CharterOrder tgt) {
		tgt.setFlightLegs(src.getFlightLegs());
		Set<FleetCandidate> fleetCandidates = src.getCandidates();
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
			tgt.setCandidates(fleetCandidates);
		}
	}

	@Override
	protected void onOrderCandidateOffered(CharterOrder order) {
		eventBus.post(new OrderEvent(OrderEvent.EventType.FLEET_OFFERED, order));
	}

	// XXX
	// @Override
	// public Page<CharterOrder> listTenantOrders(String tenantId, Status status, int page, int pageSize) {
	// if (Order.Status.DELETED == status) {
	// return Page.emptyPage(page, pageSize);
	// }
	// Page<FleetCandidate> data = null;
	// if (status == null) {
	// data = Pages.adapt(candidateRepository.findDistinctOrderByVendorIdAndStatusNotOrderByOrderCreationDateDesc(
	// tenantId, AircraftCandidate.Status.DELETED, Pages.createPageRequest(page, pageSize)));
	// }
	// else {
	// data = Pages.adapt(
	// candidateRepository.findDistinctOrderByVendorIdAndOrderStatusOrderByOrderCreationDateDesc(tenantId,
	// status, Pages.createPageRequest(page, pageSize)));
	// }
	// LOG.debug("Before filtering {} candidate data: {}", tenantId, data);
	// Map<String, CharterOrder> result = new LinkedHashMap<>();
	// data.getContent().stream().forEach(theCandidate -> {
	// // make copy
	// CharterOrder order = doCloneOrder(theCandidate);
	// // remove candidate whose owner is NOT current tenant
	// // means tenant can only see his own candidate of this order
	// Set<FleetCandidate> candidates = order.getCandidates().stream()
	// .filter(candidate -> candidate.isOwnedByTenant(tenantId)).collect(Collectors.toSet());
	// order.setCandidates(candidates);
	// result.putIfAbsent(order.getId(), order);
	// });
	// LOG.debug("Final quick flight order: {}", result);
	// return Pages.createPage(page, pageSize, data.getTotalRecords(), ImmutableList.copyOf(result.values()));
	// }

	@Override
	protected Code orderNotFoundCode() {
		return Codes.CHARTER_ORDER_NOT_FOUND;
	}

	@Override
	protected CharterOrder doCloneOrder(FleetCandidate candidate) {
		return candidate.getOrder().clone();
	}

	// TODO
	// @Transactional
	// @CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	// @Override
	// public CharterOrder selectFleetCandidate(String orderId, String fleetCandidateId) {
	// CharterOrder charterOrder = findCharterOrder(orderId);
	// Optional<FleetCandidate> selected = charterOrder.selectFleetCandidate(fleetCandidateId);
	// if (!selected.isPresent()) {
	// LOG.error("selectFleetCandidate failed, the requested fleetCandidateId: {} not found", fleetCandidateId);
	// throw new AirException(Codes.ORDER_ITEM_CANDIDATE_NOT_FOUND, M.msg(M.FLEET_CANDIDATE_NOT_FOUND));
	// }
	// BigDecimal totalPrice = selected.get().getOfferedPrice();
	// charterOrder.setTotalPrice(totalPrice);
	// return doUpdateOrderStatus(charterOrder, Order.Status.CUSTOMER_CONFIRMED);
	// }
	// @Transactional
	// @CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	// @Override
	// public CharterOrder offerFleetCandidate(String orderId, String fleetCandidateId, BigDecimal offeredAmount) {
	// ensureTotalAmount(offeredAmount);
	// CharterOrder charterOrder = findCharterOrder(orderId);
	// Optional<FleetCandidate> offered = charterOrder.offerFleetCandidate(fleetCandidateId, offeredAmount);
	// if (!offered.isPresent()) {
	// LOG.error("offerFleetCandidate failed, the requested fleetCandidateId: {} not found", fleetCandidateId);
	// throw new AirException(Codes.ORDER_ITEM_CANDIDATE_NOT_FOUND, M.msg(M.FLEET_CANDIDATE_NOT_FOUND));
	// }
	// CharterOrder charterOrderSaved = safeExecute(() -> charterOrderRepository.save(charterOrder),
	// "Offer FleetCandidate %s for order %s failed", fleetCandidateId, orderId);
	// eventBus.post(new OrderEvent(OrderEvent.EventType.FLEET_OFFERED, charterOrder));
	// return charterOrderSaved;
	// }
	// @Transactional
	// @CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	// @Override
	// public CharterOrder refuseFleetCandidate(String orderId, String fleetCandidateId) {
	// CharterOrder charterOrder = findCharterOrder(orderId);
	// Optional<FleetCandidate> refused = charterOrder.refuseFleetCandidate(fleetCandidateId);
	// if (!refused.isPresent()) {
	// LOG.error("refuseFleetCandidate failed, the requested fleetCandidateId: {} not found", fleetCandidateId);
	// throw new AirException(Codes.ORDER_ITEM_CANDIDATE_NOT_FOUND, M.msg(M.FLEET_CANDIDATE_NOT_FOUND));
	// }
	// CharterOrder charterOrderSaved = safeExecute(() -> charterOrderRepository.save(charterOrder),
	// "Refuse FleetCandidate %s for order %s failed", fleetCandidateId, orderId);
	// return charterOrderSaved;
	// }

	// NOTE: not use default, just override (special case for CharterOrder)
	// @Transactional
	// @CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	// @Override
	// public CharterOrder updateOrderStatus(String orderId, Order.Status status) {
	// if (Order.Status.DELETED == status) {
	// // update all fleetCandidates as deleted when an order is deleted
	// List<FleetCandidate> fleetCandidates = fleetCandidateRepository.findByOrderId(orderId);
	// fleetCandidates.stream().forEach(fleetCandidate -> fleetCandidate.setStatus(FleetCandidate.Status.DELETED));
	// safeExecute(() -> fleetCandidateRepository.save(fleetCandidates),
	// "updateCharterOrder %s status to %s failed", orderId, status);
	// }
	// return doUpdateOrderStatus(orderId, status);
	// }
	// @Override
	// public Function<String, CharterOrder> getOrderFinder() {
	// return orderId -> charterOrderRepository.findOne(orderId);
	// }
	//
	// @Override
	// public Function<CharterOrder, CharterOrder> getOrderPersister() {
	// return t -> charterOrderRepository.save(t);
	// }
}
