package net.aircommunity.platform.service.internal.product;

import java.util.Date;
import java.util.EnumSet;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.DomainEvent.DomainType;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.Product.Category;
import net.aircommunity.platform.model.domain.ProductFamily;
import net.aircommunity.platform.model.domain.Reviewable.ReviewStatus;
import net.aircommunity.platform.model.domain.Tenant;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.repository.ProductFamilyRepository;
import net.aircommunity.platform.service.internal.AbstractServiceSupport;
import net.aircommunity.platform.service.internal.Pages;
import net.aircommunity.platform.service.product.ProductFamilyService;

/**
 * ProductFamily Service Implementation
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional(readOnly = true)
public class ProductFamilyServiceImpl extends AbstractServiceSupport implements ProductFamilyService {
	private static final String CACHE_NAME = "cache.product-family";

	@Resource
	private ProductFamilyRepository productFamilyRepository;

	@PostConstruct
	private void init() {
		// clear cache on tenant updated
		registerCacheEvictOnDomainEvent(CACHE_NAME, EnumSet.of(DomainType.TENANT));
	}

	@Transactional
	@Override
	public ProductFamily createProductFamily(String tenantId, ProductFamily productFamily) {
		Tenant tenant = findAccount(tenantId, Tenant.class);
		ProductFamily newProductFamily = new ProductFamily();
		copyProperties(productFamily, newProductFamily);
		newProductFamily.setCreationDate(new Date());
		newProductFamily.setReviewStatus(ReviewStatus.PENDING);
		newProductFamily.setVendor(tenant);
		return safeExecute(() -> productFamilyRepository.save(newProductFamily), "Create %s for tenant %s failed",
				productFamily, tenantId);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public ProductFamily findProductFamily(String productFamilyId) {
		ProductFamily productFamily = productFamilyRepository.findOne(productFamilyId);
		if (productFamily == null) {
			throw new AirException(Codes.PRODUCT_FAMILY_NOT_FOUND, M.msg(M.PRODUCT_FAMILY_NOT_FOUND, productFamilyId));
		}
		return productFamily;
	}

	@Transactional
	@CachePut(cacheNames = CACHE_NAME, key = "#productFamilyId")
	@Override
	public ProductFamily updateProductFamily(String productFamilyId, ProductFamily newProductFamily) {
		ProductFamily productFamily = findProductFamily(productFamilyId);
		copyProperties(newProductFamily, productFamily);
		return safeExecute(() -> productFamilyRepository.save(productFamily), "Update product family %s to %s failed",
				productFamilyId, productFamily);
	}

	@Transactional
	@CachePut(cacheNames = CACHE_NAME, key = "#productFamilyId")
	@Override
	public ProductFamily reviewProductFamily(String productFamilyId, ReviewStatus reviewStatus, String rejectedReason) {
		ProductFamily productFamily = findProductFamily(productFamilyId);
		productFamily.setReviewStatus(reviewStatus);
		if (reviewStatus != ReviewStatus.APPROVED) {
			productFamily.setRejectedReason(rejectedReason);
		}
		return safeExecute(() -> productFamilyRepository.save(productFamily), "Review product family %s to %s failed",
				productFamilyId, reviewStatus);
	}

	private void copyProperties(ProductFamily src, ProductFamily tgt) {
		tgt.setCategory(src.getCategory());
		tgt.setDescription(src.getDescription());
		tgt.setImage(src.getImage());
		tgt.setName(src.getName());
	}

	@Override
	public Page<ProductFamily> listAllProductFamilies(ReviewStatus reviewStatus, int page, int pageSize) {
		if (reviewStatus == null) {
			return Pages.adapt(productFamilyRepository.findAll(createPageRequest(page, pageSize)));
		}
		return Pages.adapt(productFamilyRepository.findByReviewStatus(reviewStatus, createPageRequest(page, pageSize)));
	}

	@Override
	public long countAllProductFamilies(ReviewStatus reviewStatus) {
		if (reviewStatus == null) {
			return productFamilyRepository.count();
		}
		return productFamilyRepository.countByReviewStatus(reviewStatus);
	}

	@Override
	public Page<ProductFamily> listTenantProductFamilies(String tenantId, ReviewStatus reviewStatus, Category category,
			int page, int pageSize) {
		if (reviewStatus == null) {
			// filter by category
			if (category == null) {
				return Pages.adapt(productFamilyRepository.findByVendorId(tenantId, createPageRequest(page, pageSize)));
			}
			return Pages.adapt(productFamilyRepository.findByVendorIdAndCategory(tenantId, category,
					createPageRequest(page, pageSize)));
		}
		// filter by reviewStatus and then by category
		if (category == null) {
			return Pages.adapt(productFamilyRepository.findByVendorIdAndReviewStatus(tenantId, reviewStatus,
					createPageRequest(page, pageSize)));
		}
		return Pages.adapt(productFamilyRepository.findByVendorIdAndReviewStatusAndCategory(tenantId, reviewStatus,
				category, createPageRequest(page, pageSize)));
	}

	@Override
	public long countTenantProductFamilies(String tenantId, ReviewStatus reviewStatus) {
		if (reviewStatus == null) {
			return productFamilyRepository.countByVendorId(tenantId);
		}
		return productFamilyRepository.countByVendorIdAndReviewStatus(tenantId, reviewStatus);
	}

	@Override
	public Page<ProductFamily> listProductFamilies(Category category, int page, int pageSize) {
		if (category == null) {
			return Pages.adapt(productFamilyRepository.findByReviewStatus(ReviewStatus.APPROVED,
					createPageRequest(page, pageSize)));
		}
		return Pages.adapt(productFamilyRepository.findByReviewStatusAndCategory(ReviewStatus.APPROVED, category,
				createPageRequest(page, pageSize)));
	}

	@Transactional
	@CacheEvict(cacheNames = CACHE_NAME, key = "#productFamilyId")
	@Override
	public void deleteProductFamily(String productFamilyId) {
		safeDeletion(productFamilyRepository, productFamilyId, Codes.PRODUCT_FAMILY_CANNOT_BE_DELETED,
				M.msg(M.PRODUCT_FAMILY_CANNOT_BE_DELETED));
	}

	@Transactional
	@CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	@Override
	public void deleteProductFamilies(String tenantId) {
		safeDeletion(productFamilyRepository, () -> productFamilyRepository.deleteByVendorId(tenantId),
				Codes.PRODUCT_FAMILY_CANNOT_BE_DELETED, M.msg(M.TENANT_PRODUCT_FAMILIES_CANNOT_BE_DELETED));
	}

}
