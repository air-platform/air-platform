package net.aircommunity.platform.service.internal.product;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.model.domain.Reviewable.ReviewStatus;
import net.aircommunity.platform.model.metrics.PerProductMetrics;
import net.aircommunity.platform.model.metrics.ProductMetrics;
import net.aircommunity.platform.repository.AirTaxiRepository;
import net.aircommunity.platform.repository.AirTourRepository;
import net.aircommunity.platform.repository.AirTransportRepository;
import net.aircommunity.platform.repository.BaseProductRepository;
import net.aircommunity.platform.repository.CourseRepository;
import net.aircommunity.platform.repository.FerryFlightRepository;
import net.aircommunity.platform.repository.FleetRepository;
import net.aircommunity.platform.repository.JetTravelRepository;
import net.aircommunity.platform.repository.SchoolRepository;
import net.aircommunity.platform.service.product.CommonProductService;

/**
 * Common product service provides the generic shared product functionalities.
 *
 * @author Bin.Zhang
 */
@Service
@Transactional(readOnly = true)
public class CommonProductServiceImpl extends AbstractBaseProductService<Product> implements CommonProductService {

	@Resource
	private AirTaxiRepository airTaxiRepository;

	@Resource
	private AirTourRepository airTourRepository;

	@Resource
	private AirTransportRepository airTransportRepository;

	@Resource
	private FleetRepository fleetRepository;

	@Resource
	private FerryFlightRepository ferryFlightRepository;

	@Resource
	private JetTravelRepository jetTravelRepository;

	@Resource
	private CourseRepository courseRepository;

	@Resource
	private SchoolRepository schoolRepository;

	@Resource
	private BaseProductRepository<Product> baseProductRepository;

	@Override
	public ProductMetrics getProductMetrics() {
		PerProductMetrics fleetMetrics = buildPerProductMetrics(fleetRepository);
		PerProductMetrics ferryflightMetrics = buildPerProductMetrics(ferryFlightRepository);
		PerProductMetrics jettravelMetrics = buildPerProductMetrics(jetTravelRepository);
		PerProductMetrics airtaxiMetrics = buildPerProductMetrics(airTaxiRepository);
		PerProductMetrics airtourMetrics = buildPerProductMetrics(airTourRepository);
		PerProductMetrics airtransportMetrics = buildPerProductMetrics(airTransportRepository);
		PerProductMetrics courseMetrics = buildPerProductMetrics(courseRepository);
		// school is not product
		long schoolCount = schoolRepository.count();
		return ProductMetrics.builder().setSchoolCount(schoolCount)
				.setPerProductMetrics(Product.Type.FLEET, fleetMetrics)
				.setPerProductMetrics(Product.Type.FERRYFLIGHT, ferryflightMetrics)
				.setPerProductMetrics(Product.Type.JETTRAVEL, jettravelMetrics)
				.setPerProductMetrics(Product.Type.AIRTAXI, airtaxiMetrics)
				.setPerProductMetrics(Product.Type.AIRTOUR, airtourMetrics)
				.setPerProductMetrics(Product.Type.AIRTRANSPORT, airtransportMetrics)
				.setPerProductMetrics(Product.Type.COURSE, courseMetrics).buildAndSum();
	}

	@Override
	public ProductMetrics getProductMetrics(String tenantId) {
		PerProductMetrics fleetMetrics = buildPerProductMetrics(tenantId, fleetRepository);
		PerProductMetrics ferryflightMetrics = buildPerProductMetrics(tenantId, ferryFlightRepository);
		PerProductMetrics jettravelMetrics = buildPerProductMetrics(tenantId, jetTravelRepository);
		PerProductMetrics airtaxiMetrics = buildPerProductMetrics(tenantId, airTaxiRepository);
		PerProductMetrics airtourMetrics = buildPerProductMetrics(tenantId, airTourRepository);
		PerProductMetrics airtransportMetrics = buildPerProductMetrics(tenantId, airTransportRepository);
		PerProductMetrics courseMetrics = buildPerProductMetrics(tenantId, courseRepository);
		// school is not product
		long schoolCount = schoolRepository.countByVendorId(tenantId);
		return ProductMetrics.builder().setSchoolCount(schoolCount)
				.setPerProductMetrics(Product.Type.FLEET, fleetMetrics)
				.setPerProductMetrics(Product.Type.FERRYFLIGHT, ferryflightMetrics)
				.setPerProductMetrics(Product.Type.JETTRAVEL, jettravelMetrics)
				.setPerProductMetrics(Product.Type.AIRTAXI, airtaxiMetrics)
				.setPerProductMetrics(Product.Type.AIRTOUR, airtourMetrics)
				.setPerProductMetrics(Product.Type.AIRTRANSPORT, airtransportMetrics)
				.setPerProductMetrics(Product.Type.COURSE, courseMetrics).buildAndSum();
	}

	@SuppressWarnings("rawtypes")
	private PerProductMetrics buildPerProductMetrics(String tenantId, BaseProductRepository repository) {
		long totalCount = repository.countByVendorId(tenantId);
		long publishedCount = repository.countByVendorIdAndPublished(tenantId, true);
		long unpublishedCount = repository.countByVendorIdAndPublished(tenantId, false);
		long reviewApprovedCount = repository.countByVendorIdAndReviewStatus(tenantId, ReviewStatus.APPROVED);
		long reviewPendingCount = repository.countByVendorIdAndReviewStatus(tenantId, ReviewStatus.PENDING);
		long reviewRejectedCount = repository.countByVendorIdAndReviewStatus(tenantId, ReviewStatus.REJECTED);
		return PerProductMetrics.builder().setTotalCount(totalCount).setPublishedCount(publishedCount)
				.setUnpublishedCount(unpublishedCount).setReviewApprovedCount(reviewApprovedCount)
				.setReviewPendingCount(reviewPendingCount).setReviewRejectedCount(reviewRejectedCount).build();
	}

	@SuppressWarnings("rawtypes")
	private PerProductMetrics buildPerProductMetrics(BaseProductRepository repository) {
		long totalCount = repository.count();
		long publishedCount = repository.countByPublished(true);
		long unpublishedCount = repository.countByPublished(false);
		long reviewApprovedCount = repository.countByReviewStatus(ReviewStatus.APPROVED);
		long reviewPendingCount = repository.countByReviewStatus(ReviewStatus.PENDING);
		long reviewRejectedCount = repository.countByReviewStatus(ReviewStatus.REJECTED);
		return PerProductMetrics.builder().setTotalCount(totalCount).setPublishedCount(publishedCount)
				.setUnpublishedCount(unpublishedCount).setReviewApprovedCount(reviewApprovedCount)
				.setReviewPendingCount(reviewPendingCount).setReviewRejectedCount(reviewRejectedCount).build();
	}

	@Override
	protected BaseProductRepository<Product> getProductRepository() {
		return baseProductRepository;
	}

}
