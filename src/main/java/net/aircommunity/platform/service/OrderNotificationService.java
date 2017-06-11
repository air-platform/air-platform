package net.aircommunity.platform.service;

import java.util.Set;

import javax.annotation.Nonnull;

import net.aircommunity.platform.model.Contact;
import net.aircommunity.platform.model.OrderEvent;

/**
 * Order notification service.
 * 
 * @author Bin.Zhang
 */
public interface OrderNotificationService {

	/**
	 * Notify customer about the order event.
	 * 
	 * @param customerContacts the customer contacts
	 * @param event the order event
	 */
	void notifyCustomer(@Nonnull Set<Contact> customerContacts, @Nonnull OrderEvent event);

	/**
	 * Notify clientManagers whenever an order event happens on a product.
	 * 
	 * @param clientManagers the client managers of tenant or platform
	 * @param event the order event
	 */
	void notifyClientManager(@Nonnull Set<Contact> clientManagers, @Nonnull OrderEvent event);

}
