package net.aircommunity.platform.repository;

import net.aircommunity.platform.model.domain.CitySite;
import net.aircommunity.platform.model.domain.VenueCategoryProduct;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for {@link CitySite} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 *
 * @author Xiangwen.Kong
 */
public interface VenueCategoryProductRepository extends JpaRepository<VenueCategoryProduct, String> {


}
