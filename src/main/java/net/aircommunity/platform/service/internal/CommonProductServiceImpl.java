package net.aircommunity.platform.service.internal;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Product;
import net.aircommunity.platform.model.ProductFaq;
import net.aircommunity.platform.repository.BaseProductRepository;
import net.aircommunity.platform.service.CommonProductService;

/**
 * Common ProductService
 *
 * @author Bin.Zhang
 */
@Service
@Transactional
public class CommonProductServiceImpl extends AbstractProductService<Product> implements CommonProductService {

	protected static final String CACHE_NAME_PRD_FAQ = "cache.product-faq";

	// TODO improve cache per tenant basis

	@Resource
	private BaseProductRepository<Product> baseProductRepository;

	// *******
	// Product
	// *******
	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public Product findProduct(String productId) {
		return doFindProduct(productId);
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#productId")
	@Override
	public Product publishProduct(String productId, boolean published) {
		return doPublishProduct(productId, published);
	}

	@CacheEvict(cacheNames = CACHE_NAME, key = "#productId")
	@Override
	public void deleteProduct(String productId) {
		doDeleteProduct(productId);
	}

	@CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	@Override
	public void deleteProducts(String tenantId) {
		doDeleteProducts(tenantId);
	}

	// *******
	// FAQ
	// *******

	@Override
	public Page<ProductFaq> listProductFaqs(String productId, int page, int pageSize) {
		return doListProductFaqs(productId, page, pageSize);
	}

	@Override
	public ProductFaq createProductFaq(String productId, ProductFaq faq) {
		return doCreateProductFaq(productId, faq);
	}

	@Cacheable(cacheNames = CACHE_NAME_PRD_FAQ)
	@Override
	public ProductFaq findProductFaq(String productFaqId) {
		return doFindProductFaq(productFaqId);
	}

	@CachePut(cacheNames = CACHE_NAME_PRD_FAQ, key = "#productFaqId")
	@Override
	public ProductFaq updateProductFaq(String productFaqId, ProductFaq newFaq) {
		return doUpdateProductFaq(productFaqId, newFaq);
	}

	@CacheEvict(cacheNames = CACHE_NAME_PRD_FAQ, key = "#productFaqId")
	@Override
	public void deleteProductFaq(String productFaqId) {
		doDeleteProductFaq(productFaqId);
	}

	@CacheEvict(cacheNames = CACHE_NAME_PRD_FAQ, allEntries = true)
	@Override
	public void deleteProductFaqs(String productId) {
		doDeleteProductFaqs(productId);
	}

	@Override
	protected Code productNotFoundCode() {
		return Codes.PRODUCT_NOT_FOUND;
	}

	@Override
	protected BaseProductRepository<Product> getProductRepository() {
		return baseProductRepository;
	}

}