package net.aircommunity.platform.service.internal.payment;

import static net.aircommunity.platform.model.payment.Payments.ACTION_TRADE_SYNC;
import static net.aircommunity.platform.model.payment.Payments.KEY_PAYMENT_ACTION;
import static net.aircommunity.platform.model.payment.Payments.KEY_PAYMENT_METHOD;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.eventbus.Subscribe;

import io.micro.common.DateFormats;
import net.aircommunity.platform.Configuration;
import net.aircommunity.platform.model.TradeSyncEvent;
import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.model.domain.OrderRef;
import net.aircommunity.platform.model.domain.Payment;
import net.aircommunity.platform.model.domain.Refund;
import net.aircommunity.platform.model.domain.Trade;
import net.aircommunity.platform.model.payment.Payments;
import net.aircommunity.platform.model.payment.TradeQueryResult;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.repository.OrderRefRepository;
import net.aircommunity.platform.service.internal.AbstractServiceSupport;
import net.aircommunity.platform.service.internal.Pages;
import net.aircommunity.platform.service.order.CommonOrderService;
import net.aircommunity.platform.service.payment.PaymentService;
import net.aircommunity.platform.service.payment.PaymentSynchronizer;

/**
 * Scheduled task to query order payment info from payment gateway and sync payment status locally.
 * 
 * @author Bin.Zhang
 */
@Component
public class PaymentSynchronizerImpl extends AbstractServiceSupport implements PaymentSynchronizer {
	private static final Logger LOG = LoggerFactory.getLogger(Payments.LOGGER_NAME);

	private static final int SYNC_BATCH_SIZE = 20;
	private static final SimpleDateFormat SAFE_FORMATTER = DateFormats.simple("yyyy-MM-dd");

	@Resource
	private OrderRefRepository orderRefRepository;

	@Resource(name = "commonOrderService")
	private CommonOrderService commonOrderService;

	@Resource
	private PaymentService paymentService;

	@PostConstruct
	private void init() {
		eventBus.register(this);
	}

	@Subscribe
	private void onTradeSyncEvent(TradeSyncEvent event) {
		MDC.put(KEY_PAYMENT_ACTION, ACTION_TRADE_SYNC);
		LOG.info("Received trade sync event: {}", event);
		//
		Order order = event.getOrder();
		TradeQueryResult result = event.getQueryResult();
		MDC.put(KEY_PAYMENT_METHOD, result.getTradeMethod().name());
		try {
			switch (result.getTradeType()) {
			case PAYMENT:
				Payment payment = result.createPayment();
				payment.setStatus(Trade.Status.SUCCESS);
				payment.setNote(M.msg(M.PAYMENT_SUCCESS_SERVER_QUERY));
				commonOrderService.acceptOrderPayment(order, payment);
				LOG.info("Accepted payment sync: {}", payment);
				break;

			case REFUND:
				Refund refund = result.createRefund();
				refund.setStatus(Trade.Status.SUCCESS);
				refund.setNote(result.getNote());
				refund.setRefundResult(M.msg(M.REFUND_SUCCESS_SERVER_QUERY));
				refund.setRefundReason(order.getRefundReason());
				commonOrderService.acceptOrderRefund(order, refund);
				LOG.info("Accepted refund sync: {}", refund);
				break;

			default: // noops
			}
		}
		finally {
			MDC.remove(KEY_PAYMENT_ACTION);
			MDC.remove(KEY_PAYMENT_METHOD);
		}
	}

	/**
	 * Task (or schedule via TaskScheduler API)
	 */
	// The pattern is a list of six single space-separated fields: representing
	// second, minute, hour, day, month, weekday.
	// Month and weekday names can be given as the first three letters of the English names.
	// ---------------------------------------------------------------------------
	// | * = means match any
	// | */X = means every X
	// | ? = no specific value
	// ---------------------------------------------------------------------------
	// Spring CRON syntax example:
	// ---------------------------------------------------------------------------
	// | "0 0 * * * *" = the top of every hour of every day.
	// | "*/10 * * * * *" = every ten seconds.
	// | "0 0 8-10 * * *" = 8, 9 and 10 o'clock of every day.
	// | "0 0/30 8-10 * * *" = 8:00, 8:30, 9:00, 9:30 and 10 o'clock every day.
	// | "0 0 9-17 * * MON-FRI" = on the hour nine-to-five weekdays
	// | "0 0 0 25 12 ?" = every Christmas Day at midnight
	// ---------------------------------------------------------------------------
	@Scheduled(cron = "${air.order.payment-sync-schedule}", zone = "${air.time-zone}")
	public void scheduleTradeSync() {
		syncPayments(0);
	}

	@Override
	public void syncPayments(int recentDays) {
		MDC.put(KEY_PAYMENT_ACTION, ACTION_TRADE_SYNC);
		try {
			LocalDate sinceDate = LocalDate.now();
			if (recentDays > 0) {
				sinceDate.minusDays(recentDays);
			}
			Date date = Date.from(sinceDate.atStartOfDay(Configuration.getZoneId()).toInstant());
			LOG.info("Sync recent ({}) days (since date: {}) pending payments and refunds...", recentDays,
					SAFE_FORMATTER.format(date));
			Pageable pageable = Pages.createPageRequest(1, SYNC_BATCH_SIZE);
			// payment
			Slice<OrderRef> paymentPendingOrders = null;
			do {
				paymentPendingOrders = orderRefRepository.findPaymentPendingOrders(date, pageable);
				LOG.debug("Got {} orders to sync payment", paymentPendingOrders.getNumberOfElements());
				paymentPendingOrders.getContent().stream().forEach(orderRef -> {
					handleOrderSync(orderRef, (method, order) -> paymentService.queryPayment(method, order));
				});
				pageable = paymentPendingOrders.nextPageable();
			} while (paymentPendingOrders != null && paymentPendingOrders.hasNext());

			// refund
			pageable = Pages.createPageRequest(1, SYNC_BATCH_SIZE);
			Slice<OrderRef> refundPendingOrders = null;
			do {
				refundPendingOrders = orderRefRepository.findRefundPendingOrders(date, pageable);
				LOG.debug("Got {} orders to sync refund", refundPendingOrders.getNumberOfElements());
				refundPendingOrders.getContent().stream().forEach(orderRef -> {
					handleOrderSync(orderRef, (method, order) -> paymentService.queryRefund(method, order));
				});
				pageable = refundPendingOrders.nextPageable();
			} while (refundPendingOrders != null && refundPendingOrders.hasNext());
		}
		catch (Exception e) {
			LOG.error("Got exception when sync payments:" + e.getMessage(), e);
		}
		finally {
			MDC.remove(KEY_PAYMENT_ACTION);
		}
	}

	// ********************************************
	// FIXME: data streaming (NOT USED FOR NOW)
	// ********************************************
	// Exception: Streaming result set com.mysql.jdbc.RowDataDynamic@1ae4f7c1
	// is still active. No statements may be issued when any streaming result sets are open and in use on a given
	// connection. Ensure that you have called .close() on any active streaming result sets before attempting more
	// queries.
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	public void syncPaymentsDaily() {
		MDC.put(KEY_PAYMENT_ACTION, ACTION_TRADE_SYNC);
		try {
			Date today = Date.from(LocalDate.now().atStartOfDay(Configuration.getZoneId()).toInstant());
			LOG.debug("Sync daily ({}) pending payments...", today);
			// payment
			try (Stream<OrderRef> paymentPendingOrders = orderRefRepository.findPaymentPendingOrders(today)) {
				paymentPendingOrders.forEach(orderRef -> {
					handleOrderSync(orderRef, (method, order) -> paymentService.queryPayment(method, order));
				});
			}
			// refund
			try (Stream<OrderRef> refundPendingOrders = orderRefRepository.findRefundPendingOrders(today)) {
				refundPendingOrders.forEach(orderRef -> {
					handleOrderSync(orderRef, (method, order) -> paymentService.queryRefund(method, order));
				});
			}
		}
		finally {
			MDC.remove(KEY_PAYMENT_ACTION);
		}
	}

	private void handleOrderSync(OrderRef orderRef,
			BiFunction<Trade.Method, Order, Optional<TradeQueryResult>> handler) {
		LOG.debug("Syncing... trade for {}", orderRef);
		Trade.Method method = orderRef.getMethod();
		if (method == null) {
			LOG.warn("No trade method is available for {}, sync skipped", orderRef);
			return;
		}
		commonOrderService.lookupByOrderNo(orderRef.getOrderNo()).ifPresent(order -> {
			handler.apply(method, order).ifPresent(result -> eventBus.post(new TradeSyncEvent(result, order)));
		});
	}
}
