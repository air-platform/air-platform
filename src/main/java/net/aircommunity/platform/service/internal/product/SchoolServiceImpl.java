package net.aircommunity.platform.service.internal.product;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.School;
import net.aircommunity.platform.model.domain.Tenant;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.repository.SchoolRepository;
import net.aircommunity.platform.service.internal.AbstractServiceSupport;
import net.aircommunity.platform.service.internal.Pages;
import net.aircommunity.platform.service.product.SchoolService;

/**
 * School service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional(readOnly = true)
public class SchoolServiceImpl extends AbstractServiceSupport implements SchoolService {
	private static final String CACHE_NAME = "cache.school";

	@Resource
	private SchoolRepository schoolRepository;

	@Transactional
	@Override
	public School createSchool(String tenantId, School school) {
		Tenant tenant = findAccount(tenantId, Tenant.class);
		School newSchool = new School();
		copyProperties(school, newSchool);
		newSchool.setVendor(tenant);
		return safeExecute(() -> schoolRepository.save(newSchool), "Create school %s for tenant %s failed", school,
				tenantId);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public School findSchool(String schoolId) {
		School school = schoolRepository.findOne(schoolId);
		if (school == null) {
			throw new AirException(Codes.SCHOOL_NOT_FOUND, M.msg(M.SCHOOL_NOT_FOUND));
		}
		return school;
	}

	@Transactional
	@CachePut(cacheNames = CACHE_NAME, key = "#schoolId")
	@Override
	public School updateSchool(String schoolId, School newSchool) {
		School school = findSchool(schoolId);
		copyProperties(newSchool, school);
		return safeExecute(() -> schoolRepository.save(school), "Update school %s to %s failed,", schoolId, newSchool);
	}

	private void copyProperties(School src, School tgt) {
		tgt.setName(src.getName());
		tgt.setImage(src.getImage());
		tgt.setAddress(src.getAddress());
		tgt.setContact(src.getContact());
		tgt.setDescription(src.getDescription());
		tgt.setBaseDescription(src.getBaseDescription());
	}

	@Override
	public Page<School> listAllSchools(int page, int pageSize) {
		return Pages.adapt(schoolRepository.findAll(createPageRequest(page, pageSize)));
	}

	@Override
	public Page<School> listSchools(String tenantId, int page, int pageSize) {
		return Pages.adapt(schoolRepository.findByVendorId(tenantId, createPageRequest(page, pageSize)));
	}

	@Transactional
	@CacheEvict(cacheNames = CACHE_NAME, key = "#schoolId")
	@Override
	public void deleteSchool(String schoolId) {
		// NOTE: we can only delete this school if there is no course created on this school
		safeDeletion(schoolRepository, schoolId, Codes.SCHOOL_CANNOT_BE_DELETED, M.msg(M.SCHOOL_CANNOT_BE_DELETED));
	}

	@Transactional
	@CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	@Override
	public void deleteSchools(String tenantId) {
		safeDeletion(schoolRepository, () -> schoolRepository.deleteByVendorId(tenantId),
				Codes.SCHOOL_CANNOT_BE_DELETED, M.msg(M.TENANT_SCHOOLS_CANNOT_BE_DELETED, tenantId));
	}
}
