package net.aircommunity.platform.service.internal;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.common.collect.ImmutableCollectors;
import net.aircommunity.platform.model.CharterOrder;
import net.aircommunity.platform.model.Fleet;
import net.aircommunity.platform.model.FleetCandidate;
import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.User;
import net.aircommunity.platform.repository.CharterOrderRepository;
import net.aircommunity.platform.repository.FleetCandidateRepository;
import net.aircommunity.platform.repository.FleetRepository;
import net.aircommunity.platform.service.CharterOrderService;

/**
 * CharterOrder service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional
public class CharterOrderServiceImpl extends AbstractServiceSupport implements CharterOrderService {

	@Resource
	private CharterOrderRepository charterOrderRepository;

	@Resource
	private FleetCandidateRepository fleetCandidateRepository;

	@Resource
	private FleetRepository fleetRepository;

	@Override
	public CharterOrder createCharterOrder(String userId, CharterOrder charterOrder) {
		User owner = findAccount(userId, User.class);
		CharterOrder newCharterOrder = new CharterOrder();
		newCharterOrder.setCreationDate(new Date());
		newCharterOrder.setStatus(Order.Status.PENDING);
		copyProperties(charterOrder, newCharterOrder);
		// set vendor
		newCharterOrder.setOwner(owner);
		return charterOrderRepository.save(newCharterOrder);
	}

	/**
	 * Copy properties from src to tgt without ID
	 */
	private void copyProperties(CharterOrder src, CharterOrder tgt) {
		tgt.setContact(src.getContact());
		tgt.setFlightLegs(src.getFlightLegs());
		Set<FleetCandidate> fleetCandidates = src.getFleetCandidates();
		if (fleetCandidates != null) {
			fleetCandidates.stream().forEach(fleetCandidate -> {
				Fleet fleet = fleetCandidate.getFleet();
				if (fleet != null) {
					fleet = fleetRepository.findOne(fleet.getId());
					fleetCandidate.setFleet(fleet);
				}
			});
		}
		tgt.setFleetCandidates(fleetCandidates);
		tgt.setNote(src.getNote());
	}

	@Override
	public CharterOrder findCharterOrder(String charterOrderId) {
		CharterOrder charterOrder = charterOrderRepository.findOne(charterOrderId);
		if (charterOrder == null) {
			throw new AirException(Codes.CHARTERORDER_NOT_FOUND,
					String.format("CharterOrder %s is not found", charterOrderId));
		}
		return charterOrder;
	}

	@Override
	public CharterOrder findCharterOrderByOrderNo(String orderNo) {
		CharterOrder charterOrder = charterOrderRepository.findByOrderNo(orderNo);
		if (charterOrder == null) {
			throw new AirException(Codes.CHARTERORDER_NOT_FOUND,
					String.format("CharterOrder NO: %s is not found", orderNo));
		}
		return charterOrder;
	}

	@Override
	public CharterOrder updateCharterOrder(String charterOrderId, CharterOrder newCharterOrder) {
		CharterOrder charterOrder = findCharterOrder(charterOrderId);
		copyProperties(newCharterOrder, charterOrder);
		return charterOrderRepository.save(charterOrder);
	}

	@Override
	public CharterOrder updateCharterOrderStatus(String charterOrderId, Order.Status status) {
		CharterOrder charterOrder = findCharterOrder(charterOrderId);
		charterOrder.setStatus(status);
		return charterOrderRepository.save(charterOrder);
	}

	@Override
	public Page<CharterOrder> listTenantCharterOrders(String tenantId, int page, int pageSize) {
		Page<FleetCandidate> data = Pages
				.adapt(fleetCandidateRepository.findByVendorId(tenantId, Pages.createPageRequest(page, pageSize)));
		List<CharterOrder> content = data.getContent().stream().map(fleetCandidate -> fleetCandidate.getOrder())
				.collect(ImmutableCollectors.toList());
		return Pages.createPage(page, pageSize, data.getTotalRecords(), content);
	}

	@Override
	public Page<CharterOrder> listCharterOrders(String userId, int page, int pageSize) {
		User owner = findAccount(userId, User.class);
		// Page<CharterOrder> p = Pages.adapt(charterOrderRepository.findByOwnerIdByOrderByCreationDateDesc(userId,
		// Pages.createPageRequest(page, pageSize)));
		Page<CharterOrder> p = Pages.adapt(charterOrderRepository.findBy2OwnerByOrderByCreationDateDesc(owner,
				Pages.createPageRequest(page, pageSize)));

		return p;
	}

	@Override
	public Page<CharterOrder> listCharterOrders(int page, int pageSize) {
		return Pages.adapt(charterOrderRepository.findAll(Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public void deleteCharterOrder(String charterOrderId) {
		charterOrderRepository.delete(charterOrderId);
	}

	@Override
	public void deleteCharterOrders(String userId) {
		charterOrderRepository.deleteByOwnerId(userId);
	}

}
