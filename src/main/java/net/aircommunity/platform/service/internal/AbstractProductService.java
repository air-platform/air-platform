package net.aircommunity.platform.service.internal;

import java.lang.reflect.ParameterizedType;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Product;
import net.aircommunity.platform.model.Tenant;
import net.aircommunity.platform.repository.BaseProductRepository;

/**
 * Abstract Product service support.
 * 
 * @author Bin.Zhang
 */
abstract class AbstractProductService<T extends Product> extends AbstractServiceSupport {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractProductService.class);

	protected Class<T> type;

	@PostConstruct
	@SuppressWarnings("unchecked")
	private void init() {
		ParameterizedType pt = ParameterizedType.class.cast(getClass().getGenericSuperclass());
		type = (Class<T>) pt.getActualTypeArguments()[0];
	}

	protected abstract Code productNotFoundCode();

	protected abstract BaseProductRepository<T> getProductRepository();

	// *********************
	// Generic CRUD shared
	// *********************

	protected T createProduct(String tenantId, T product) {
		Tenant vendor = findAccount(tenantId, Tenant.class);
		T newProduct = null;
		try {
			newProduct = type.newInstance();
		}
		catch (Exception unexpected) {
			LOG.error(String.format("Failed to create instance %s for user %s, cause: ", type, tenantId,
					unexpected.getMessage()), unexpected);
			throw new AirException(Codes.INTERNAL_ERROR,
					String.format("Failed to create instance %s", type.getSimpleName()));
		}
		newProduct.setName(product.getName());
		newProduct.setImage(product.getImage());
		newProduct.setDescription(product.getDescription());
		copyProperties(product, newProduct);
		// set props cannot be overridden by subclass
		newProduct.setCreationDate(new Date());
		newProduct.setVendor(vendor);
		try {
			return getProductRepository().save(newProduct);
		}
		catch (Exception e) {
			LOG.error(String.format("Create %s: %s for user %s failed, cause: ", type.getSimpleName(), tenantId,
					e.getMessage()), e);
			throw new AirException(Codes.INTERNAL_ERROR,
					String.format("Create %s: %s failed", type.getSimpleName(), product));
		}
	}

	protected T findProduct(String productId) {
		T product = getProductRepository().findOne(productId);
		if (product == null) {
			throw new AirException(productNotFoundCode(),
					String.format("%s: %s is not found", type.getSimpleName(), productId));
		}
		return product;
	}

	protected T updateProduct(String productId, T newProduct) {
		T product = findProduct(productId);
		product.setName(newProduct.getName());
		product.setImage(newProduct.getImage());
		product.setDescription(newProduct.getDescription());
		copyProperties(newProduct, product);
		try {
			return getProductRepository().save(product);
		}
		catch (Exception e) {
			LOG.error(String.format("Update %s: %s with %s failed, cause: ", type.getSimpleName(), productId,
					newProduct, e.getMessage()), e);
			throw new AirException(Codes.INTERNAL_ERROR,
					String.format("Update %s: %s failed", type.getSimpleName(), productId));
		}
	}

	protected void copyProperties(T src, T tgt) {
	}

	/**
	 * For USER (Exclude Products in DELETED status)
	 */
	protected Page<T> listTenantProducts(String tenantId, int page, int pageSize) {
		return Pages.adapt(getProductRepository().findByVendorIdOrderByCreationDateDesc(tenantId,
				Pages.createPageRequest(page, pageSize)));
	}

	/**
	 * For ADMIN (Products of any tenant)
	 */
	protected Page<T> listAllProducts(int page, int pageSize) {
		return Pages.adapt(
				getProductRepository().findAllByOrderByCreationDateDesc(Pages.createPageRequest(page, pageSize)));
	}

	protected void deleteProduct(String productId) {
		getProductRepository().delete(productId);
	}

	protected void deleteProducts(String tenantId) {
		getProductRepository().deleteByVendorId(tenantId);
	}
}
