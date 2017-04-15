package net.aircommunity.platform.service.internal;

import javax.annotation.Nonnull;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.School;
import net.aircommunity.platform.model.Tenant;
import net.aircommunity.platform.repository.SchoolRepository;
import net.aircommunity.platform.repository.TenantRepository;
import net.aircommunity.platform.service.AccountService;
import net.aircommunity.platform.service.SchoolService;

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

	@Override
	public School createSchool(String tenantId, School request) {
		Tenant tenant = findAccount(tenantId, Tenant.class);
		// FIXME create new School
		request.setTenant(tenant);
		return schoolRepository.save(request);
	}

	@Override
	public School findSchool(@Nonnull String schoolId) {
		School school = schoolRepository.findOne(schoolId);
		if (school == null) {
			throw new AirException(Codes.SCHOOL_NOT_FOUND, String.format("school %s not found", schoolId));
		}
		return school;
	}

	@Nonnull
	@Override
	public School updateSchool(String schoolId, School newSchool) {
		School school = findSchool(schoolId);
		school.setImageUrl(newSchool.getImageUrl());
		school.setContact(newSchool.getContact());
		school.setAddress(newSchool.getAddress());
		school.setSchoolName(newSchool.getSchoolName());
		school.setSchoolDesc(newSchool.getSchoolDesc());
		return schoolRepository.save(school);
	}

	@Override
	public Page<School> listSchools(int page, int pageSize) {
		return Pages.adapt(schoolRepository.findAll(Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<School> listSchools(String tenantId, int page, int pageSize) {
		Tenant tenant = findAccount(tenantId, Tenant.class);
		return Pages.adapt(schoolRepository.findByTenant(tenant, Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public void deleteSchool(String schoolId) {
		schoolRepository.delete(schoolId);
	}
}
