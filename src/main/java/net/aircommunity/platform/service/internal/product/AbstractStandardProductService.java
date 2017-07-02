package net.aircommunity.platform.service.internal.product;

import org.springframework.cache.annotation.CachePut;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.service.product.StandardProductService;

/**
 * Abstract standard product service with creation and update support (should be extended by a concrete product
 * service).
 * 
 * @author Bin.Zhang
 * @param <T> the product type
 */
abstract class AbstractStandardProductService<T extends Product> extends AbstractBaseProductService<T>
		implements StandardProductService<T> {

	@Transactional
	@Override
	public T createProduct(String tenantId, T product) {
		return doCreateProduct(tenantId, product);
	}

	@Transactional
	@CachePut(cacheNames = CACHE_NAME, key = "#productId")
	@Override
	public T updateProduct(String productId, T newProduct) {
		return doUpdateProduct(productId, newProduct);
	}

}
