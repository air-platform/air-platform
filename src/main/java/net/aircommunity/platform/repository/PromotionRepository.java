package net.aircommunity.platform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.domain.Promotion;
import net.aircommunity.platform.model.domain.Product.Category;

/**
 * Repository interface for {@link Promotion} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface PromotionRepository extends JpaRepository<Promotion, String> {

	List<Promotion> findByCategory(Category category);

}
