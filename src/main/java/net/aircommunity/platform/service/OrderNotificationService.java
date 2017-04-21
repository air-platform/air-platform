package net.aircommunity.platform.service;

import java.util.Set;

import javax.annotation.Nonnull;

import net.aircommunity.platform.model.Contact;
import net.aircommunity.platform.model.Order;

/**
 * Order notification service.
 * 
 * @author Bin.Zhang
 */
public interface OrderNotificationService {

	/**
	 * Notify clientManagers when order is placed on a product. The client managers MUST IN FORMAT OF: person1:email1,
	 * person2:email2, ..., personN:emailN.
	 * 
	 * @param clientManagers the client managers
	 * @param order the order
	 */
	void notifyClientManager(@Nonnull Set<Contact> clientManagers, @Nonnull Order order);

}
