package net.aircommunity.platform.service.internal.order;

import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.springframework.cache.annotation.CachePut;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.OrderEvent;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.Aircraft;
import net.aircommunity.platform.model.domain.AircraftCandidate;
import net.aircommunity.platform.model.domain.FleetCandidate;
import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.model.domain.QuickFlightOrder;
import net.aircommunity.platform.repository.AircraftCandidateRepository;
import net.aircommunity.platform.repository.QuickFlightOrderRepository;
import net.aircommunity.platform.service.internal.Pages;
import net.aircommunity.platform.service.order.QuickFlightOrderService;
import net.aircommunity.platform.service.order.annotation.ManagedOrderService;
import net.aircommunity.platform.service.product.AircraftService;

/**
 * Quick flight order service implementation.
 * 
 * @author Bin.Zhang
 */
@ManagedOrderService(Product.Type.QUICKFLIGHT)
@Transactional(readOnly = true)
public class QuickFlightOrderServiceImpl extends AbstractOrderCandidateService<AircraftCandidate, QuickFlightOrder>
		implements QuickFlightOrderService {

	@Resource
	private AircraftService aircraftService;

	@Inject
	public QuickFlightOrderServiceImpl(QuickFlightOrderRepository orderRepository,
			AircraftCandidateRepository candidateRepository) {
		super(orderRepository, candidateRepository);
	}

	@Override
	protected void copyProperties(QuickFlightOrder src, QuickFlightOrder tgt) {
		tgt.setArrivalCity(src.getArrivalCity());
		tgt.setArrivalLocation(src.getArrivalLocation());
		tgt.setDepartureDate(src.getDepartureDate());
		tgt.setDepartureCity(src.getDepartureCity());
		tgt.setDepartureLocation(src.getDepartureLocation());
		tgt.setPassengers(src.getPassengers());
	}

	@Transactional
	@CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public QuickFlightOrder initiateOrderCandidates(String orderId, Set<AircraftCandidate> candidates) {
		QuickFlightOrder quickFlightOrder = findQuickFlightOrder(orderId);
		if (candidates == null || candidates.isEmpty()) {
			return quickFlightOrder;
		}
		// apply candidates
		candidates.stream().forEach(candidate -> {
			Aircraft aircraft = candidate.getAircraft();
			aircraft = aircraftService.findAircraft(aircraft.getId());
			candidate.setAircraft(aircraft);
			// force CANDIDATE on initiation
			candidate.setStatus(FleetCandidate.Status.CANDIDATE);
		});
		quickFlightOrder.initiateCandidates(candidates);
		QuickFlightOrder quickFlightOrderSaved = safeExecute(() -> getOrderRepository().save(quickFlightOrder),
				"Initiate AircraftCandidate %s for order %s failed", orderId, orderId);
		// notify platform admin
		eventBus.post(new OrderEvent(OrderEvent.EventType.AIRCRAFT_OFFERED, quickFlightOrder));
		return quickFlightOrderSaved;
	}

	@Transactional
	@CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public QuickFlightOrder promoteOrderCandidates(String orderId, Set<String> candidateIds) {
		QuickFlightOrder quickFlightOrder = findQuickFlightOrder(orderId);
		quickFlightOrder.promoteCandidate(candidateIds);
		QuickFlightOrder orderSaved = safeExecute(() -> getOrderRepository().save(quickFlightOrder),
				"Promote aircraft candidates %s for order %s failed", Joiner.on(",").skipNulls().join(candidateIds),
				orderId);
		onOrderCandidateOffered(orderSaved);
		return orderSaved;
	}

	@Override
	protected void onOrderCandidateOffered(QuickFlightOrder order) {
		eventBus.post(new OrderEvent(OrderEvent.EventType.QUICK_FLIGHT_OFFERED, order));
	}

	@Override
	public Page<QuickFlightOrder> listTenantUnconfirmedOrders(String tenantId, int page, int pageSize) {
		Page<QuickFlightOrder> data = Pages.adapt(orderRepository
				.findByStatusOrderByCreationDateDesc(Order.Status.CREATED, Pages.createPageRequest(page, pageSize)));
		ImmutableList.Builder<QuickFlightOrder> clonedData = ImmutableList.builder();
		// filter candidates that only belongs to this tenant
		data.getContent().stream().forEach(order -> {
			QuickFlightOrder cloned = order.clone();
			Set<AircraftCandidate> candidates = cloned.getCandidates().stream()
					.filter(candidate -> candidate.isOwnedByTenant(tenantId)).collect(Collectors.toSet());
			cloned.setCandidates(candidates);
			clonedData.add(cloned);
		});
		return Pages.createPage(data.getPage(), data.getPageSize(), data.getTotalRecords(), clonedData.build());
	}

	// @Override
	// public Page<QuickFlightOrder> listTenantOrders(String tenantId, Status status, int page, int pageSize) {
	// // TODO list all user orders for quickFlight
	// // TODO add order status: PLATFORM_PROMPTED ? after prompt, tenant owner can only offer price, and invisible for
	// // non-owner
	// if (Order.Status.DELETED == status) {
	// return Page.emptyPage(page, pageSize);
	// }
	// Page<AircraftCandidate> data = null;
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
	// Map<String, QuickFlightOrder> result = new LinkedHashMap<>();
	// data.getContent().stream().forEach(theCandidate -> {
	// // make copy
	// QuickFlightOrder order = doCloneOrder(theCandidate);
	// // remove candidate whose owner is NOT current tenant
	// // means tenant can only see his own candidate of this order
	// Set<AircraftCandidate> candidates = order.getCandidates().stream()
	// .filter(candidate -> candidate.isOwnedByTenant(tenantId)).collect(Collectors.toSet());
	// order.setCandidates(candidates);
	// result.putIfAbsent(order.getId(), order);
	// });
	// LOG.debug("Final quick flight order: {}", result);
	// return Pages.createPage(data.getPage(), data.getPageSize(), data.getTotalRecords(),
	// ImmutableList.copyOf(result.values()));
	// }

	@Override
	protected Code orderNotFoundCode() {
		return Codes.QUICKFLIGHT_ORDER_NOT_FOUND;
	}

	@Override
	protected QuickFlightOrder doCloneOrder(AircraftCandidate candidate) {
		return candidate.getOrder().clone();
	}
}
