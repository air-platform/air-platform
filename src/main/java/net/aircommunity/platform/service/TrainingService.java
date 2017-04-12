package net.aircommunity.platform.service;

import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.TrainingBanner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by guankai on 11/04/2017.
 */
public interface TrainingService {
    @Nonnull
    Page<TrainingBanner> getAllTrainingBanner(int page, int pageSize);

    @Nonnull
    TrainingBanner createTrainingBanner(@Nonnull TrainingBanner trainingBanner);
}
