package net.aircommunity.platform.service.internal;

import javax.annotation.Resource;

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

	@Resource
	private BaseProductRepository<Product> baseProductRepository;

	// *******
	// Product
	// *******
	@Override
	public Product findProduct(String productId) {
		return doFindProduct(productId);
	}

	@Override
	public Product publishProduct(String productId, boolean published) {
		return doPublishProduct(productId, published);
	}

	@Override
	public void deleteProduct(String productId) {
		doDeleteProduct(productId);
	}

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

	@Override
	public ProductFaq findProductFaq(String productFaqId) {
		return doFindProductFaq(productFaqId);
	}

	@Override
	public ProductFaq updateProductFaq(String productFaqId, ProductFaq newFaq) {
		return doUpdateProductFaq(productFaqId, newFaq);
	}

	@Override
	public void deleteProductFaq(String productFaqId) {
		doDeleteProductFaq(productFaqId);
	}

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
