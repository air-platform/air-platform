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

    @Nonnull
    Page<School> getSchoolList(int page, int pageSize);

    @Nonnull
    School createSchool(@Nonnull School request,@Nonnull String tenantId);

    @Nonnull
    School updateSchool(@Nonnull School request,@Nonnull String tenantId);


}
