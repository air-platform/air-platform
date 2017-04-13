package net.aircommunity.platform.service;

import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.School;
import net.aircommunity.platform.model.Tenant;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;

/**
 * Created by guankai on 11/04/2017.
 */
public interface SchoolService {
    /**
     * get all school list
     * @param page
     * @param pageSize
     * @return
     */
    @Nonnull
    Page<School> getSchoolList(int page, int pageSize);

    /**
     * get all school list by tenant
     * @param tenantId
     * @param page
     * @param pageSize
     * @return
     */
    @Nonnull
    Page<School> getSchoolListByTenant(String tenantId, int page, int pageSize);

    /**
     * create a school by tenant
     * @param request
     * @param tenantId
     * @return
     */
    @Nonnull
    School createSchool(@Nonnull School request, @Nonnull String tenantId);

    /**
     * update a school
     * @param request
     * @param tenantId
     * @return
     */
    @Nonnull
    School updateSchool(@Nonnull School request, @Nonnull String tenantId);


}
