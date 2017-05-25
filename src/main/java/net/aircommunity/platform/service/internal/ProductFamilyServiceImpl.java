package net.aircommunity.platform.service.internal;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
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
		newProductFamily.setCategory(productFamily.getCategory());
		newProductFamily.setDescription(productFamily.getDescription());
		newProductFamily.setImage(productFamily.getImage());
		newProductFamily.setName(productFamily.getName());
		newProductFamily.setPublished(false);
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
		productFamily.setCategory(newProductFamily.getCategory());
		productFamily.setDescription(newProductFamily.getDescription());
		productFamily.setImage(newProductFamily.getImage());
		productFamily.setName(newProductFamily.getName());
		return safeExecute(() -> productFamilyRepository.save(productFamily), "Update product family %s to %s failed",
				productFamilyId, productFamily);
	}

	@Override
	public List<ProductFamily> listProductFamilies(String tenantId) {
		return productFamilyRepository.findByVendorId(tenantId);
	}

	@Override
	public List<ProductFamily> listProductFamiliesByCategory(String tenantId, Category category) {
		return productFamilyRepository.findByVendorIdAndCategory(tenantId, category);
	}

	@Override
	public List<ProductFamily> listAllProductFamilies() {
		return productFamilyRepository.findAll();
	}

	@Override
	public List<ProductFamily> listAllProductFamiliesByCategory(Category category) {
		return productFamilyRepository.findByCategory(category);
	}

	@CacheEvict(cacheNames = CACHE_NAME, key = "#productFamilyId")
	@Override
	public void deleteProductFamily(String productFamilyId) {
		safeExecute(() -> productFamilyRepository.delete(productFamilyId), "Delete product family %s failed",
				productFamilyId);
	}

	@CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	@Override
	public void deleteProductFamilys(String tenantId) {
		safeExecute(() -> productFamilyRepository.deleteByVendorId(tenantId),
				"Delete all product families for tenant %s failed", tenantId);
	}

}
