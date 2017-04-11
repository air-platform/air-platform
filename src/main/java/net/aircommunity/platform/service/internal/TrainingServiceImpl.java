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
    public TrainingBanner createTrainingBanner(@Nonnull String bannerName, @Nonnull String bannerDesc, @Nonnull String imageUrl, @Nullable String bannerUrl) {
        TrainingBanner trainingBanner = new TrainingBanner();
        trainingBanner.setBannerName(bannerName);
        trainingBanner.setBannerDesc(bannerDesc);
        trainingBanner.setImageUrl(imageUrl);
        trainingBanner.setBannerUrl(bannerUrl);
        TrainingBanner trainingBannerCreated = trainingBannerRepository.save(trainingBanner);
        return trainingBannerCreated;
    }
}
