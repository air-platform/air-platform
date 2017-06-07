package net.aircommunity.platform.service;

import javax.annotation.Nonnull;

import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.School;

/**
 * Created by guankai on 11/04/2017.
 */
public interface SchoolService {

	/**
	 * create a school by tenant
	 * @param request
	 * @param tenantId
	 * @return
	 */
	@Nonnull
	School createSchool(@Nonnull String tenantId, @Nonnull School school);

	/**
	 * get school by id
	 * @param schoolId
	 * @return
	 */
	@Nonnull
	School findSchool(@Nonnull String schoolId);

	/**
	 * update a school
	 * @param request
	 * @return
	 */
	@Nonnull
	School updateSchool(@Nonnull String schoolId, @Nonnull School newSchool);

	/**
	 * get all school list
	 * @param page
	 * @param pageSize
	 * @return
	 */
	@Nonnull
	Page<School> listAllSchools(int page, int pageSize);

	/**
	 * get all school list by tenant
	 * @param tenantId
	 * @param page
	 * @param pageSize
	 * @return
	 */
	@Nonnull
	Page<School> listSchools(@Nonnull String tenantId, int page, int pageSize);

	/**
	 * Delete a School.
	 * 
	 * @param schoolId the schoolId
	 */
	void deleteSchool(@Nonnull String schoolId);

	/**
	 * Delete all Schools.
	 * 
	 * @param tenantId the tenantId
	 */
	void deleteSchools(@Nonnull String tenantId);

}
