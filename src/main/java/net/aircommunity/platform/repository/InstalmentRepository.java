package net.aircommunity.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.Instalment;

/**
 * Repository interface for {@link Instalment} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface InstalmentRepository extends JpaRepository<Instalment, String> {

}
