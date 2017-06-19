package net.aircommunity.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.domain.Tenant;

/**
 * Created by guankai on 11/04/2017.
 */
public interface TenantRepository extends JpaRepository<Tenant, String> {
}
