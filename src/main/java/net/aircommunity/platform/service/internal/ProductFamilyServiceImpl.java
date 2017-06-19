package net.aircommunity.platform.service.internal;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.ProductFamily;
import net.aircommunity.platform.model.domain.Tenant;
import net.aircommunity.platform.model.domain.Product.Category;
import net.aircommunity.platform.model.domain.Reviewable.ReviewStatus;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.repository.ProductFamilyRepository;
import net.aircommunity.platform.service.ProductFamilyService;

/**
 * ProductFamily Service Implementation
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional
public class ProductFamilyServiceImpl extends AbstractServiceSupport implements ProductFamilyService {
	private static final String CACHE_NAME = "cache.productfamily";

	@Resource
	private ProductFamilyRepository productFamilyRepository;

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

	@CachePut(cacheNames = CACHE_NAME, key = "#productFamilyId")
	@Override
	public ProductFamily updateProductFamily(String productFamilyId, ProductFamily newProductFamily) {
		ProductFamily productFamily = findProductFamily(productFamilyId);
		copyProperties(newProductFamily, productFamily);
		return safeExecute(() -> productFamilyRepository.save(productFamily), "Update product family %s to %s failed",
				productFamilyId, productFamily);
	}

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
			return Pages.adapt(
					productFamilyRepository.findAllByOrderByCreationDateDesc(Pages.createPageRequest(page, pageSize)));
		}
		return Pages.adapt(productFamilyRepository.findByReviewStatusOrderByCreationDateDesc(reviewStatus,
				Pages.createPageRequest(page, pageSize)));
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
				return Pages.adapt(productFamilyRepository.findByVendorIdOrderByCreationDateDesc(tenantId,
						Pages.createPageRequest(page, pageSize)));
			}
			return Pages.adapt(productFamilyRepository.findByVendorIdAndCategoryOrderByCreationDateDesc(tenantId,
					category, Pages.createPageRequest(page, pageSize)));
		}
		// filter by reviewStatus and then by category
		if (category == null) {
			return Pages.adapt(productFamilyRepository.findByVendorIdAndReviewStatusOrderByCreationDateDesc(tenantId,
					reviewStatus, Pages.createPageRequest(page, pageSize)));
		}
		return Pages.adapt(productFamilyRepository.findByVendorIdAndReviewStatusAndCategoryOrderByCreationDateDesc(
				tenantId, reviewStatus, category, Pages.createPageRequest(page, pageSize)));
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
			return Pages.adapt(productFamilyRepository.findByReviewStatusOrderByCreationDateDesc(ReviewStatus.APPROVED,
					Pages.createPageRequest(page, pageSize)));
		}
		return Pages.adapt(productFamilyRepository.findByReviewStatusAndCategoryOrderByCreationDateDesc(
				ReviewStatus.APPROVED, category, Pages.createPageRequest(page, pageSize)));
	}

	@CacheEvict(cacheNames = CACHE_NAME, key = "#productFamilyId")
	@Override
	public void deleteProductFamily(String productFamilyId) {
		safeExecute(() -> productFamilyRepository.delete(productFamilyId), "Delete product family %s failed",
				productFamilyId);
	}

	@CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	@Override
	public void deleteProductFamilies(String tenantId) {
		safeExecute(() -> productFamilyRepository.deleteByVendorId(tenantId),
				"Delete all product families for tenant %s failed", tenantId);
	}

}
