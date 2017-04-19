package net.aircommunity.platform.service.internal;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.micro.common.collect.ImmutableCollectors;
import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.CharterOrder;
import net.aircommunity.platform.model.Fleet;
import net.aircommunity.platform.model.FleetCandidate;
import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Order.Status;
import net.aircommunity.platform.model.Page;
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
		return createOrder(userId, order);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public CharterOrder findCharterOrder(String orderId) {
		return findOrder(orderId);
	}

	@Override
	public CharterOrder findCharterOrderByOrderNo(String orderNo) {
		return findOrderByOrderNo(orderNo);
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public CharterOrder updateCharterOrder(String orderId, CharterOrder newOrder) {
		return updateOrder(orderId, newOrder);
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public CharterOrder updateCharterOrderStatus(String orderId, Order.Status status) {
		CharterOrder charterOrder = findCharterOrder(orderId);
		charterOrder.setStatus(status);
		switch (status) {
		case PENDING:
			break;

		case PAID:
			charterOrder.setPaymentDate(new Date());
			break;

		case FINISHED:
			charterOrder.setFinishedDate(new Date());
			break;

		case CANCELLED:
			break;

		default:
		}
		return charterOrderRepository.save(charterOrder);
	}

	/**
	 * Copy properties from src to tgt without ID
	 */
	@Override
	protected void copyProperties(CharterOrder src, CharterOrder tgt) {
		tgt.setContact(src.getContact());
		tgt.setNote(src.getNote());
		tgt.setFlightLegs(src.getFlightLegs());
		Set<FleetCandidate> fleetCandidates = src.getFleetCandidates();
		if (fleetCandidates != null) {
			fleetCandidates.stream().forEach(fleetCandidate -> {
				Fleet fleet = fleetCandidate.getFleet();
				if (fleet != null) {
					fleet = fleetService.findFleet(fleet.getId());
					fleetCandidate.setFleet(fleet);
				}
			});
			tgt.setFleetCandidates(fleetCandidates);
		}
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public CharterOrder selectFleetCandidate(String orderId, String fleetCandidateId) {
		CharterOrder charterOrder = findCharterOrder(orderId);
		charterOrder.selectFleetCandidate(fleetCandidateId);
		return charterOrderRepository.save(charterOrder);

		// FleetCandidate fleetCandidate = fleetCandidateRepository.findOne(fleetCandidateId);
		// boolean selected = fleetCandidateRepository.isFleetCandidateSelected(orderId);
		// if (selected) {
		// return;
		// }
		// if (fleetCandidate != null) {
		// fleetCandidate.setStatus(FleetCandidate.Status.SELECTED);
		// fleetCandidateRepository.save(fleetCandidate);
		// }
	}

	@Override
	public Page<CharterOrder> listUserCharterOrders(String userId, Status status, int page, int pageSize) {
		return listUserOrders(userId, status, page, pageSize);
	}

	@Override
	public Page<CharterOrder> listTenantCharterOrders(String tenantId, Status status, int page, int pageSize) {
		Page<FleetCandidate> data = null;
		if (status == null) {
			data = Pages
					.adapt(fleetCandidateRepository.findByVendorId(tenantId, Pages.createPageRequest(page, pageSize)));
		}
		else {
			data = Pages.adapt(fleetCandidateRepository.findByVendorIdAndOrderStatus(tenantId, status,
					Pages.createPageRequest(page, pageSize)));
		}
		List<CharterOrder> content = data.getContent().stream().map(fleetCandidate -> fleetCandidate.getOrder())
				.collect(ImmutableCollectors.toList());
		return Pages.createPage(page, pageSize, data.getTotalRecords(), content);
	}

	@Override
	public Page<CharterOrder> listCharterOrders(Status status, int page, int pageSize) {
		return listAllOrders(status, page, pageSize);
	}

	@CacheEvict(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public void deleteCharterOrder(String orderId) {
		deleteOrder(orderId);
	}

	@CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	@Override
	public void deleteCharterOrders(String userId) {
		deleteOrders(userId);
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
