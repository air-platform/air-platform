package net.aircommunity.platform.service.order;

import java.util.function.Function;

import javax.annotation.Nonnull;

import net.aircommunity.platform.model.domain.Order;

/**
 * Order operation internal use only.
 * 
 * @author Bin.Zhang
 * @param <T> the order type
 */
public interface InternalOrderOperation<T extends Order> {

	/**
	 * Find the order with the given orderId. The finder will return null if the order is not found, and the deleted
	 * status will be also returned AS IS.
	 * 
	 * @return finder function
	 */
	@Nonnull
	Function<String, T> getOrderFinder();

	/**
	 * Saves a given entity. Use the returned instance for further operations as the save operation might have changed
	 * the entity instance completely. The function accept entity as input and returns the saved entity
	 * 
	 * @return persister function
	 */
	@Nonnull
	Function<T, T> getOrderPersister();

}
