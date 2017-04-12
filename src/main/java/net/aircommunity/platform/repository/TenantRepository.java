package net.aircommunity.platform.repository;

import net.aircommunity.platform.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by guankai on 11/04/2017.
 */
public interface TenantRepository extends JpaRepository<Tenant, String> {
}
