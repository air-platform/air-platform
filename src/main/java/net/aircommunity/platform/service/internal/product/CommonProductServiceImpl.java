package net.aircommunity.platform.service.internal.product;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.repository.BaseProductRepository;
import net.aircommunity.platform.service.product.CommonProductService;

/**
 * Common product service provides the generic shared product functionalities.
 *
 * @author Bin.Zhang
 */
@Service
@Transactional
public class CommonProductServiceImpl extends AbstractBaseProductService<Product> implements CommonProductService {

	@Resource
	private BaseProductRepository<Product> baseProductRepository;

	@Override
	protected BaseProductRepository<Product> getProductRepository() {
		return baseProductRepository;
	}

}
