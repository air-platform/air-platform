package net.aircommunity.platform.service.internal;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.AirTourOrder;
import net.aircommunity.platform.model.Order.Status;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Passenger;
import net.aircommunity.platform.model.User;
import net.aircommunity.platform.repository.AirTourOrderRepository;
import net.aircommunity.platform.repository.PassengerRepository;
import net.aircommunity.platform.service.AirTourOrderService;
import net.aircommunity.platform.service.AirTourService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * AirTourOrder service implementation.
 *
 * @author Bin.Zhang
 */
@Service
@Transactional
public class AirTourOrderServiceImpl extends AbstractServiceSupport implements AirTourOrderService {
    private static final String CACHE_NAME = "cache.AirTourOrder";

    @Resource
    private AirTourOrderRepository airTourOrderRepository;
    @Resource
    private PassengerRepository passengerRepository;

    @Resource
    private AirTourService airTourService;

    @Override
    public AirTourOrder createAirTourOrder(String userId, AirTourOrder AirTourOrder) {
        User owner = findAccount(userId, User.class);
        AirTourOrder newAirTourOrder = new AirTourOrder();
        newAirTourOrder.setCreationDate(new Date());
        newAirTourOrder.setStatus(Status.PENDING);
        copyProperties(AirTourOrder, newAirTourOrder);
        // set vendor
        newAirTourOrder.setOwner(owner);
        return airTourOrderRepository.save(newAirTourOrder);
    }

    @Cacheable(cacheNames = CACHE_NAME)
    @Override
    public AirTourOrder findAirTourOrder(String airTourOrderId) {
        AirTourOrder airTourOrder = airTourOrderRepository.findOne(airTourOrderId);
        if (airTourOrder == null) {
            throw new AirException(Codes.TOUR_ORDER_NOT_FOUND,
                    String.format("AirTourOrder %s is not found", airTourOrderId));
        }
        return airTourOrder;
    }

    @CachePut(cacheNames = CACHE_NAME, key = "#AirTourOrderId")
    @Override
    public AirTourOrder updateAirTourOrder(String airTourOrderId, AirTourOrder newAirTourOrder) {
        AirTourOrder airTourOrder = findAirTourOrder(airTourOrderId);
        copyProperties(newAirTourOrder, airTourOrder);
        return airTourOrderRepository.save(airTourOrder);
    }

    private void copyProperties(AirTourOrder src, AirTourOrder tgt) {
        tgt.setAirTour(src.getAirTour());
        tgt.setNote(src.getNote());
        tgt.setDate(src.getDate());
        tgt.setTimeSlot(src.getTimeSlot());
        tgt.setCreationDate(new Date());
        tgt.setStatus(Status.PUBLISHED);
        Set<Passenger> passengers = src.getPassengers();
        if (passengers != null) {
            passengers = passengers.stream().map(passenger ->
                    passengerRepository.findOne(passenger.getId())
            ).collect(Collectors.toSet());
            tgt.setPassengers(passengers);
        }
    }

    @CachePut(cacheNames = CACHE_NAME, key = "#airTourOrderId")
    @Override
    public AirTourOrder updateAirTourOrderStatus(String airTourOrderId, Status status) {
        AirTourOrder airTourOrder = findAirTourOrder(airTourOrderId);
        airTourOrder.setStatus(status);
        return airTourOrderRepository.save(airTourOrder);
    }

    @Override
    public Page<AirTourOrder> listUserAirTourOrders(String userId, Status status, int page,
                                                    int pageSize) {
        if (status == null) {
            return Pages.adapt(airTourOrderRepository.findByOwnerIdOrderByCreationDateDesc(userId,
                    Pages.createPageRequest(page, pageSize)));
        }
        return Pages.adapt(airTourOrderRepository.findByOwnerIdAndStatusOrderByCreationDateDesc(userId, status,
                Pages.createPageRequest(page, pageSize)));
    }

    @Override
    public Page<AirTourOrder> listTenantAirTourOrders(String tenantId, Status status, int page,
                                                      int pageSize) {
        if (status == null) {
            return Pages.adapt(airTourOrderRepository.findByVendorIdOrderByCreationDateDesc(tenantId,
                    Pages.createPageRequest(page, pageSize)));
        }
        return Pages.adapt(airTourOrderRepository.findByVendorIdAndStatusOrderByCreationDateDesc(tenantId, status,
                Pages.createPageRequest(page, pageSize)));
    }

    @Override
    public Page<AirTourOrder> listAirTourOrders(Status status, int page, int pageSize) {
        if (status == null) {
            return Pages.adapt(airTourOrderRepository
                    .findAllByOrderByCreationDateDesc(Pages.createPageRequest(page, pageSize)));
        }
        return Pages.adapt(airTourOrderRepository.findByStatusOrderByCreationDateDesc(status,
                Pages.createPageRequest(page, pageSize)));
    }

    @CacheEvict(cacheNames = CACHE_NAME, key = "#airTourOrderId")
    @Override
    public void deleteAirTourOrder(String airTourOrderId) {
        airTourOrderRepository.delete(airTourOrderId);
    }

    @CacheEvict(cacheNames = CACHE_NAME)
    @Override
    public void deleteAirTourOrders(String userId) {
        airTourOrderRepository.deleteByOwnerId(userId);
    }

}
