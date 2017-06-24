package net.aircommunity.platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.domain.ProductFaq;

/**
 * Repository interface for {@link ProductFaq} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface ProductFaqRepository extends JpaRepository<ProductFaq, String> {

	/**
	 * Find all FAQs of a product.
	 * 
	 * @param productId the productId
	 * @param pageable the page request
	 * @return page of ProductFAQ
	 */
	Page<ProductFaq> findByProductIdOrderByCreationDateDesc(String productId, Pageable pageable);

	/**
	 * Delete all FAQs of a product.
	 * 
	 * @param productId the productId
	 * @return the number of records deleted
	 */
	long deleteByProductId(String productId);

}
