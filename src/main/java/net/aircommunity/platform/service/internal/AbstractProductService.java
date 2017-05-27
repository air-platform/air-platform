package net.aircommunity.platform.service.internal;

import java.lang.reflect.ParameterizedType;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Product;
import net.aircommunity.platform.model.ProductFaq;
import net.aircommunity.platform.model.Tenant;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.repository.BaseProductRepository;
import net.aircommunity.platform.repository.ProductFaqRepository;

/**
 * Abstract Product service support.
 * 
 * @author Bin.Zhang
 */
@SuppressWarnings("unchecked")
abstract class AbstractProductService<T extends Product> extends AbstractServiceSupport {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractProductService.class);

	protected static final String CACHE_NAME = "cache.product";

	@Resource
	protected ProductFaqRepository productFaqRepository;

	protected Class<T> type;

	@PostConstruct
	private void init() {
		ParameterizedType pt = ParameterizedType.class.cast(getClass().getGenericSuperclass());
		type = (Class<T>) pt.getActualTypeArguments()[0];
	}

	protected abstract Code productNotFoundCode();

	protected abstract BaseProductRepository<T> getProductRepository();

	// *********************
	// Generic CRUD shared
	// *********************

	protected final T doCreateProduct(String tenantId, T product) {
		Tenant vendor = findAccount(tenantId, Tenant.class);
		T newProduct = null;
		try {
			newProduct = type.newInstance();
		}
		catch (Exception unexpected) {
			LOG.error(String.format("Failed to create instance %s for user %s, cause: %s", type, tenantId,
					unexpected.getMessage()), unexpected);
			throw newInternalException();
		}
		newProduct.setName(product.getName());
		newProduct.setImage(product.getImage());
		newProduct.setClientManagers(product.getClientManagers());
		newProduct.setDescription(product.getDescription());
		newProduct.setRank(0);
		newProduct.setPublished(false);
		copyProperties(product, newProduct);
		// set props cannot be overridden by subclass
		newProduct.setCreationDate(new Date());
		newProduct.setVendor(vendor);
		final T productToSaved = newProduct;
		return safeExecute(() -> getProductRepository().save(productToSaved), "Create %s: %s for tenant %s failed",
				type.getSimpleName(), product, tenantId);
	}

	protected final T doFindProduct(String productId) {
		T product = getProductRepository().findOne(productId);
		if (product == null) {
			LOG.error("{}: {} is not found", type.getSimpleName(), productId);
			throw new AirException(productNotFoundCode(), M.msg(M.PRODUCT_NOT_FOUND));
		}
		return product;
	}

	protected final T doUpdateProduct(String productId, T newProduct) {
		T product = doFindProduct(productId);
		product.setName(newProduct.getName());
		product.setImage(newProduct.getImage());
		product.setClientManagers(newProduct.getClientManagers());
		product.setDescription(newProduct.getDescription());
		copyProperties(newProduct, product);
		return safeExecute(() -> getProductRepository().save(product), "Update %s: %s with %s failed",
				type.getSimpleName(), productId, newProduct);
	}

	protected final T doPublishProduct(String productId, boolean published) {
		T product = doFindProduct(productId);
		try {
			product.setPublished(published);
			return getProductRepository().save(product);
		}
		catch (Exception e) {
			LOG.error(String.format("Update %s: %s with published: %b failed, cause: %s", type.getSimpleName(),
					productId, published, e.getMessage()), e);
			throw newInternalException();
		}
	}

	protected final T doUpdateProductRank(String productId, int rank) {
		T product = doFindProduct(productId);
		if (rank < 0) {
			rank = 0;
		}
		product.setRank(rank);
		return safeExecute(() -> getProductRepository().save(product), "Update %s: %s to rank: %d",
				type.getSimpleName(), productId, rank);
	}

	protected void copyProperties(T src, T tgt) {
	}

	/**
	 * For USER (Exclude Products in DELETED status)
	 */
	protected final Page<T> doListTenantProducts(String tenantId, int page, int pageSize) {
		return Pages.adapt(getProductRepository().findByVendorIdOrderByCreationDateDesc(tenantId,
				Pages.createPageRequest(page, pageSize)));
	}

	/**
	 * For ADMIN (Products of any tenant)
	 */
	protected final Page<T> doListAllProducts(int page, int pageSize) {
		return Pages.adapt(
				getProductRepository().findAllByOrderByCreationDateDesc(Pages.createPageRequest(page, pageSize)));
	}

	protected final void doDeleteProduct(String productId) {
		safeExecute(() -> getProductRepository().delete(productId), "Delete product %s failed", productId);
	}

	protected final void doDeleteProducts(String tenantId) {
		safeExecute(() -> getProductRepository().deleteByVendorId(tenantId), "Delete all products for tenant %s failed",
				tenantId);
	}

	// *********************
	// Generic CRUD FAQ
	// *********************
	protected final ProductFaq doCreateProductFaq(String productId, ProductFaq faq) {
		Product product = doFindProduct(productId);
		ProductFaq newFaq = new ProductFaq();
		newFaq.setContent(faq.getContent());
		newFaq.setDate(new Date());
		newFaq.setTitle(faq.getTitle());
		newFaq.setProduct(product);
		return safeExecute(() -> productFaqRepository.save(newFaq), "Create FAQ: %s for product %s failed", faq,
				product);
	}

	protected final ProductFaq doFindProductFaq(String productFaqId) {
		ProductFaq faq = productFaqRepository.findOne(productFaqId);
		if (faq == null) {
			LOG.error("Product FAQ: {} is not found", productFaqId);
			throw new AirException(Codes.PRODUCT_FAQ_NOT_FOUND, M.msg(M.PRODUCT_FAQ_NOT_FOUND));
		}
		return faq;
	}

	protected final ProductFaq doUpdateProductFaq(String productFaqId, ProductFaq newFaq) {
		ProductFaq faq = doFindProductFaq(productFaqId);
		faq.setContent(newFaq.getContent());
		faq.setTitle(newFaq.getTitle());
		return safeExecute(() -> productFaqRepository.save(newFaq), "Update FAQ: %s to %s failed", productFaqId,
				newFaq);
	}

	protected final Page<ProductFaq> doListProductFaqs(String productId, int page, int pageSize) {
		return Pages.adapt(productFaqRepository.findByProductIdOrderByDateDesc(productId,
				Pages.createPageRequest(page, pageSize)));
	}

	protected final void doDeleteProductFaq(String productFaqId) {
		safeExecute(() -> productFaqRepository.delete(productFaqId), "Delete FAQ: %s failed", productFaqId);
	}

	protected final void doDeleteProductFaqs(String productId) {
		safeExecute(() -> productFaqRepository.deleteByProductId(productId), "Delete FAQs for product %s failed",
				productId);
	}

}
