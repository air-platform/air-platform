package net.aircommunity.platform.service.internal;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Product.Category;
import net.aircommunity.platform.model.ProductFamily;
import net.aircommunity.platform.model.Tenant;
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
		newProductFamily.setApproved(false);
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
	public ProductFamily reviewProductFamily(String productFamilyId, boolean approved, String rejectedReason) {
		ProductFamily productFamily = findProductFamily(productFamilyId);
		productFamily.setApproved(approved);
		if (!approved) {
			productFamily.setRejectedReason(rejectedReason);
		}
		return safeExecute(() -> productFamilyRepository.save(productFamily), "Review product family %s to %b failed",
				productFamilyId, approved);
	}

	@Override
	public long countProductFamilies(boolean approved) {
		return productFamilyRepository.countByApproved(approved);
	}

	private void copyProperties(ProductFamily src, ProductFamily tgt) {
		tgt.setCategory(src.getCategory());
		tgt.setDescription(src.getDescription());
		tgt.setImage(src.getImage());
		tgt.setName(src.getName());
	}

	@Override
	public Page<ProductFamily> listProductFamilies(String tenantId, int page, int pageSize) {
		return Pages.adapt(productFamilyRepository.findByVendorId(tenantId, Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<ProductFamily> listProductFamilies(String tenantId, boolean approved, int page, int pageSize) {
		return Pages.adapt(productFamilyRepository.findByVendorIdAndApproved(tenantId, approved,
				Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<ProductFamily> listProductFamiliesByCategory(String tenantId, Category category, int page,
			int pageSize) {
		return Pages.adapt(productFamilyRepository.findByVendorIdAndCategory(tenantId, category,
				Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<ProductFamily> listProductFamiliesByCategory(String tenantId, Category category, boolean approved,
			int page, int pageSize) {
		return Pages.adapt(productFamilyRepository.findByVendorIdAndCategoryAndApproved(tenantId, category, approved,
				Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<ProductFamily> listProductFamilies(int page, int pageSize) {
		return Pages.adapt(productFamilyRepository.findAll(Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<ProductFamily> listProductFamilies(boolean approved, int page, int pageSize) {
		return Pages.adapt(productFamilyRepository.findByApproved(approved, Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<ProductFamily> listProductFamiliesByCategory(Category category, int page, int pageSize) {
		return Pages.adapt(productFamilyRepository.findByCategory(category, Pages.createPageRequest(page, pageSize)));
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
