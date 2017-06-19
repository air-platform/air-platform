package net.aircommunity.platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.domain.Banner;
import net.aircommunity.platform.model.domain.Product.Category;

/**
 * Repository interface for {@link Banner} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface BannerRepository extends JpaRepository<Banner, String> {

	Page<Banner> findByCategory(Category category, Pageable pageable);
}
