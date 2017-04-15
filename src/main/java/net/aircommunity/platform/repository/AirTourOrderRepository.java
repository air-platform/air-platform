package net.aircommunity.platform.repository;

import net.aircommunity.platform.model.AirTourOrder;
import net.aircommunity.platform.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Created by guankai on 15/04/2017.
 */
public interface AirTourOrderRepository extends BaseOrderRepository<AirTourOrder> {

    /**
     * Find all orders place on a tenant
     *
     * @param tenantId the tenantId
     * @param pageable page request
     * @return a page of FerryFlightOrders or empty if none
     */
    Page<AirTourOrder> findByVendorIdOrderByCreationDateDesc(String tenantId, Pageable pageable);

    /**
     * Find all orders place on a tenant
     *
     * @param tenantId the tenantId
     * @param status   the status
     * @param pageable page request
     * @return a page of FerryFlightOrders or empty if none
     */
    Page<AirTourOrder> findByVendorIdAndStatusOrderByCreationDateDesc(String tenantId, Order.Status status,
                                                                      Pageable pageable);

}
