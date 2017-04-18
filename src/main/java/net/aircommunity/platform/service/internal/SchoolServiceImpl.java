package net.aircommunity.platform.service.internal;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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
	private static final String CACHE_NAME = "cache.school";

	@Resource
	private SchoolRepository schoolRepository;

	@Resource
	private TenantRepository tenantRepository;

	@Resource
	private AccountService accountService;

	@Override
	public School createSchool(String tenantId, School school) {
		Tenant tenant = findAccount(tenantId, Tenant.class);
		School newSchool = new School();
		copyProperties(school, newSchool);
		newSchool.setVendor(tenant);
		return schoolRepository.save(newSchool);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public School findSchool(String schoolId) {
		School school = schoolRepository.findOne(schoolId);
		if (school == null) {
			throw new AirException(Codes.SCHOOL_NOT_FOUND, String.format("school %s not found", schoolId));
		}
		return school;
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#schoolId")
	@Override
	public School updateSchool(String schoolId, School newSchool) {
		School school = findSchool(schoolId);
		copyProperties(newSchool, school);
		return schoolRepository.save(school);
	}

	private void copyProperties(School src, School tgt) {
		tgt.setAddress(src.getAddress());
		tgt.setContact(src.getContact());
		tgt.setImage(src.getImage());
		tgt.setDescription(src.getDescription());
		tgt.setName(src.getName());
		tgt.setBaseDesc(src.getBaseDesc());
	}

	@Override
	public Page<School> listSchools(int page, int pageSize) {
		return Pages.adapt(schoolRepository.findAll(Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<School> listSchools(String tenantId, int page, int pageSize) {
		return Pages.adapt(schoolRepository.findByVendorId(tenantId, Pages.createPageRequest(page, pageSize)));
	}

	@CacheEvict(cacheNames = CACHE_NAME, key = "#schoolId")
	@Override
	public void deleteSchool(String schoolId) {
		schoolRepository.delete(schoolId);
	}

	@CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	@Override
	public void deleteSchools(String tenantId) {
		schoolRepository.deleteByVendorId(tenantId);
	}
}
