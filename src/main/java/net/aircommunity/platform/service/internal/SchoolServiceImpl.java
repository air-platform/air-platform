package net.aircommunity.platform.service.internal;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.School;
import net.aircommunity.platform.model.Tenant;
import net.aircommunity.platform.repository.SchoolRepository;
import net.aircommunity.platform.repository.TenantRepository;
import net.aircommunity.platform.service.SchoolService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Resource;

/**
 * Created by guankai on 11/04/2017.
 */
@Service
@Transactional
public class SchoolServiceImpl implements SchoolService {
    @Resource
    private SchoolRepository schoolRepository;
    @Resource
    private TenantRepository tenantRepository;

    @Nonnull
    @Override
    public Page<School> getSchoolList(int page, int pageSize) {
        return Pages.adapt(schoolRepository.findAll(Pages.createPageRequest(page, pageSize)));
    }

    @Nonnull
    @Override
    public School createSchool(@Nonnull School request, @Nonnull String tenantId) {
        Tenant tenant = tenantRepository.findOne(tenantId);
        if (tenant == null) {
            throw new AirException(Codes.ACCOUNT_NOT_FOUND, String.format("tenant %s is not found", tenantId));
        }
        request.setTenant(tenant);
        School SchoolCreated = schoolRepository.save(request);
        return SchoolCreated;
    }

    @Nonnull
    @Override
    public School updateSchool(@Nonnull School request,@Nonnull String tenantId) {
        Tenant tenant = tenantRepository.findOne(tenantId);
        if(tenant == null){
            throw new AirException(Codes.ACCOUNT_NOT_FOUND,String.format("tenant %s is not found", tenantId));
        }
        request.setTenant(tenant);
        School schoolUpdate = schoolRepository.save(request);
        return schoolUpdate;
    }
}
