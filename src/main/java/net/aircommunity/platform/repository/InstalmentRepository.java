package net.aircommunity.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import net.aircommunity.platform.model.domain.Instalment;

/**
 * Repository interface for {@link Instalment} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
@NoRepositoryBean // TODO enabled it once Instalment and InstalmentOrder is used
public interface InstalmentRepository extends JpaRepository<Instalment, String> {

}
