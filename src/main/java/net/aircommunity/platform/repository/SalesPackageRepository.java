package net.aircommunity.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.SalesPackage;

/**
 * Repository interface for {@link SalesPackage} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface SalesPackageRepository extends JpaRepository<SalesPackage, String> {

}
