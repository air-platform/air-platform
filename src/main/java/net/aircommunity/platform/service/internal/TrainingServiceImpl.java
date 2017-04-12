package net.aircommunity.platform.service.internal;

import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.TrainingBanner;
import net.aircommunity.platform.repository.TrainingBannerRepository;
import net.aircommunity.platform.service.TrainingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.util.List;

/**
 * Created by guankai on 11/04/2017.
 */
@Service
@Transactional
public class TrainingServiceImpl implements TrainingService {
    @Resource
    private TrainingBannerRepository trainingBannerRepository;

    @Nonnull
    @Override
    public Page<TrainingBanner> getAllTrainingBanner(int page, int pageSize) {
        return Pages.adapt(trainingBannerRepository.findAll(Pages.createPageRequest(page, pageSize)));
    }

    @Nonnull
    @Override
    public TrainingBanner createTrainingBanner(@Nonnull TrainingBanner request) {
        TrainingBanner trainingBannerCreated = trainingBannerRepository.save(request);
        return trainingBannerCreated;
    }
}
