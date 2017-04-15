package net.aircommunity.platform.service;

import javax.annotation.Nonnull;

import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.TrainingBanner;

/**
 * Created by guankai on 11/04/2017.
 */
public interface TrainingService {

	@Nonnull
	Page<TrainingBanner> getAllTrainingBanner(int page, int pageSize);

	@Nonnull
	TrainingBanner createTrainingBanner(@Nonnull TrainingBanner trainingBanner);
}
