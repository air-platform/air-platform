package net.aircommunity.platform.service.internal.payment;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.model.domain.OrderRef;
import net.aircommunity.platform.repository.OrderRefRepository;
import net.aircommunity.platform.service.order.CommonOrderService;

/**
 * Scheduled task to query order payment info from payment gateway and sync payment status locally.
 * 
 * @author Bin.Zhang
 */
// @Component
public class PaymentSynchronizer {
	private static final Logger LOG = LoggerFactory.getLogger(PaymentSynchronizer.class);

	@Resource
	private OrderRefRepository orderRefRepository;

	@Resource
	private CommonOrderService commonOrderService;

	/**
	 * Task
	 */
	@Transactional(readOnly = true)
	@Scheduled(fixedDelay = 5000, initialDelay = 10000)
	public void syncPayment() {
		LOG.debug("Sync payment");
		Date today = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
		try (Stream<OrderRef> paymentPendingOrders = orderRefRepository.findPaymentPendingOrders(today)) {
			paymentPendingOrders.forEach(order -> {
				String orderNo = order.getOrderNo();
				// TODO query payment gatway with orderNo
			});
		}

		try (Stream<OrderRef> refundPendingOrders = orderRefRepository.findRefundPendingOrders(today)) {
			refundPendingOrders.forEach(order -> {
				String orderNo = order.getOrderNo();
				// TODO query payment gatway with orderNo
			});
		}

		// Payment payment = new Payment();
		// payment.setAmount(totalAmount);
		// payment.setMethod(getPaymentMethod());
		// payment.setOrderNo(orderNo);
		// payment.setTradeNo(tradeNo);
		// payment.setTimestamp(paymentDate);
		// payment.setNote(M.msg(M.PAYMENT_SUCCESS_SERVER_QUERY));
		// commonOrderService.payOrder(order.getId(), payment);
	}

	public static void main(String ar[]) {

	}

}
