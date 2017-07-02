package net.aircommunity.platform.service.product;

import javax.annotation.Nonnull;

import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.School;

/**
 * School service manages {@code School}s of a {@code Tenant}.
 * 
 * @author Bin.Zhang
 */
public interface SchoolService {

	/**
	 * Create a school for a tenant.
	 * 
	 * @param tenantId the tenant id
	 * @param school the school to be created
	 * @return school created
	 */
	@Nonnull
	School createSchool(@Nonnull String tenantId, @Nonnull School school);

	/**
	 * Find school by the given schoolId
	 * 
	 * @param schoolId the schoolId
	 * @return school found
	 */
	@Nonnull
	School findSchool(@Nonnull String schoolId);

	/**
	 * Update a school of the given schoolId with a new school data.
	 * 
	 * @param schoolId the schoolId
	 * @param newSchool the newSchool data
	 * @return school updated
	 */
	@Nonnull
	School updateSchool(@Nonnull String schoolId, @Nonnull School newSchool);

	/**
	 * List all schools (ADMIN)
	 * 
	 * @param page the page number
	 * @param pageSize the page size
	 * @return a page of school or empty if none
	 */
	@Nonnull
	Page<School> listAllSchools(int page, int pageSize);

	/**
	 * List all schools for a tenant (TENANT)
	 * 
	 * @param tenantId the tenant id
	 * @param page the page number
	 * @param pageSize the page size
	 * @return a page of school or empty if none
	 */
	@Nonnull
	Page<School> listSchools(@Nonnull String tenantId, int page, int pageSize);

	/**
	 * Delete a School of the given schoolId.
	 * 
	 * @param schoolId the schoolId
	 */
	void deleteSchool(@Nonnull String schoolId);

	/**
	 * Delete all Schools of a tenant.
	 * 
	 * @param tenantId the tenantId
	 */
	void deleteSchools(@Nonnull String tenantId);

}
