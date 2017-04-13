package net.aircommunity.platform.service.internal;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.School;
import net.aircommunity.platform.model.Tenant;
import net.aircommunity.platform.repository.SchoolRepository;
import net.aircommunity.platform.repository.TenantRepository;
import net.aircommunity.platform.service.AccountService;
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
public class SchoolServiceImpl extends AbstractServiceSupport implements SchoolService {
    @Resource
    private SchoolRepository schoolRepository;
    @Resource
    private TenantRepository tenantRepository;
    @Resource
    private AccountService accountService;

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
    public School updateSchool(@Nonnull School request) {
        School school = schoolRepository.findOne(request.getId());
        if (school == null) {
            throw new AirException(Codes.ACCOUNT_NOT_FOUND, String.format("tenant %s is not found", request.getId()));
        }
        school.setImageUrl(request.getImageUrl());
        school.setContact(request.getContact());
        school.setAddress(request.getAddress());
        school.setSchoolName(request.getSchoolName());
        school.setSchoolDesc(request.getSchoolDesc());
        School schoolUpdate = schoolRepository.save(school);
        return schoolUpdate;
    }

    @Nonnull
    @Override
    public Page<School> getSchoolListByTenant(String tenantId, int page, int pageSize) {
        Tenant tenant = findAccount(tenantId, Tenant.class);
        return Pages.adapt(schoolRepository.findByTenant(tenant, Pages.createPageRequest(page, pageSize)));
    }
}
