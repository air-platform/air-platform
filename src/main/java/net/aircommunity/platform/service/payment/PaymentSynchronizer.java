package net.aircommunity.platform.service.payment;

/**
 * Payment Synchronizer.
 * 
 * @author Bin.Zhang
 */
public interface PaymentSynchronizer {

	/**
	 * Perform payment and refund sync recent N days via payment gateway query APIs, and update {@code Order}
	 * payment/refund information accordingly.
	 * 
	 * @param recentDays recent days, 0 means today
	 */
	void syncPayments(int recentDays);
}
