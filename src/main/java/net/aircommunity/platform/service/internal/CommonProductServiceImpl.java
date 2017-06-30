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
import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.model.domain.Product.Category;
import net.aircommunity.platform.model.domain.ProductFaq;
import net.aircommunity.platform.model.domain.Reviewable.ReviewStatus;
import net.aircommunity.platform.repository.BaseProductRepository;
import net.aircommunity.platform.service.AircraftService;
import net.aircommunity.platform.service.CommonProductService;
import net.aircommunity.platform.service.ProductFamilyService;
import net.aircommunity.platform.service.SchoolService;

/**
 * Common ProductService
 *
 * @author Bin.Zhang
 */
@Service
@Transactional
public class CommonProductServiceImpl extends AbstractProductService<Product> implements CommonProductService {

	protected static final String CACHE_NAME_PRD_FAQ = "cache.product-faq";

	// XXX improve cache per tenant basis

	@Resource
	private BaseProductRepository<Product> baseProductRepository;

	@Resource
	private AircraftService aircraftService;

	@Resource
	private SchoolService schoolService;

	@Resource
	private ProductFamilyService productFamilyService;

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
	public Product increaseProductSales(String productId) {
		return doUpdateProductSales(productId, 1);
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#productId")
	@Override
	public Product publishProduct(String productId, boolean published) {
		return doPublishProduct(productId, published);
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#productId")
	@Override
	public Product updateProductRank(String productId, int newRank) {
		return doUpdateProductRank(productId, newRank);
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#productId")
	@Override
	public Product updateProductScore(String productId, double score) {
		return doUpdateProductScore(productId, score);
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#productId")
	@Override
	public Product reviewProduct(String productId, ReviewStatus reviewStatus, String rejectedReason) {
		return doReviewProduct(productId, reviewStatus, rejectedReason);
	}

	@Override
	public Page<Product> listAllProducts(ReviewStatus reviewStatus, Category category, int page, int pageSize) {
		return doListAllProducts(reviewStatus, category, page, pageSize);
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

	@Override
	protected BaseProductRepository<Product> getProductRepository() {
		return baseProductRepository;
	}

}
