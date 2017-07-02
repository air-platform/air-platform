package net.aircommunity.platform.service.internal.product;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.model.domain.Product.Category;
import net.aircommunity.platform.model.domain.ProductFaq;
import net.aircommunity.platform.model.domain.Reviewable.ReviewStatus;
import net.aircommunity.platform.service.product.ProductFaqService;
import net.aircommunity.platform.service.product.ProductService;

/**
 * Base product service implementation. (products should extends this class to simplify implementation)
 * 
 * @author Bin.Zhang
 * @param <T> the product type
 */
abstract class AbstractBaseProductService<T extends Product> extends AbstractProductService<T>
		implements ProductService<T>, ProductFaqService {

	// TODO: improve cache per tenant basis and also enable cache for list ?

	// *******
	// Product
	// *******
	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public T findProduct(String productId) {
		return doFindProduct(productId);
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#productId")
	@Override
	public T increaseProductSales(String productId) {
		return doUpdateProductSales(productId, 1);
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#productId")
	@Override
	public T updateProductSales(String productId, int deltaSales) {
		return doUpdateProductSales(productId, deltaSales);
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#productId")
	@Override
	public T publishProduct(String productId, boolean published) {
		return doPublishProduct(productId, published);
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#productId")
	@Override
	public T updateProductRank(String productId, int newRank) {
		return doUpdateProductRank(productId, newRank);
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#productId")
	@Override
	public T updateProductScore(String productId, double score) {
		return doUpdateProductScore(productId, score);
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#productId")
	@Override
	public T reviewProduct(String productId, ReviewStatus reviewStatus, String rejectedReason) {
		return doReviewProduct(productId, reviewStatus, rejectedReason);
	}

	@Override
	public Page<T> listAllProducts(ReviewStatus reviewStatus, Category category, int page, int pageSize) {
		return doListAllProducts(reviewStatus, category, page, pageSize);
	}

	@Override
	public long countAllProducts(ReviewStatus reviewStatus) {
		return doCountAllProducts(reviewStatus);
	}

	@Override
	public Page<T> listTenantProducts(String tenantId, ReviewStatus reviewStatus, int page, int pageSize) {
		return doListTenantProducts(tenantId, reviewStatus, page, pageSize);
	}

	@Override
	public long countTenantProducts(String tenantId, ReviewStatus reviewStatus) {
		return doCountTenantProducts(tenantId, reviewStatus);
	}

	@Override
	public Page<T> listProducts(int page, int pageSize) {
		return doListProductsForUsers(page, pageSize);
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

	@CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	@Override
	public void purgeProducts(String tenantId) {
		doDeleteProducts(tenantId);
		aircraftService.deleteAircrafts(tenantId);
		schoolService.deleteSchools(tenantId);
		productFamilyService.deleteProductFamilies(tenantId);
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

	@CachePut(cacheNames = CACHE_NAME_PRD_FAQ, key = "#productFaqId")
	@Override
	public ProductFaq increaseProductFaqViews(String productFaqId) {
		ProductFaq faq = findProductFaq(productFaqId);
		faq.increaseViews();
		return safeExecute(() -> productFaqRepository.save(faq), "Increase FAQ views: %s failed", productFaqId);
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

}
