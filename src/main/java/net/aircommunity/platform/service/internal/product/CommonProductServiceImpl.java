package net.aircommunity.platform.service.internal.product;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableMap;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.DomainEvent.DomainType;
import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.model.domain.Product.Type;
import net.aircommunity.platform.model.domain.Reviewable.ReviewStatus;
import net.aircommunity.platform.model.metrics.PerProductMetrics;
import net.aircommunity.platform.model.metrics.ProductMetrics;
import net.aircommunity.platform.nls.M;
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
import net.aircommunity.platform.service.product.StandardProductService;
import net.aircommunity.platform.service.product.annotation.ManagedProductService;

/**
 * Common product service provides the generic shared product functionalities.
 *
 * @author Bin.Zhang
 */
@Service
@Transactional(readOnly = true)
public class CommonProductServiceImpl extends AbstractBaseProductService<Product>
		implements CommonProductService, ApplicationContextAware {
	private static final Logger LOG = LoggerFactory.getLogger(CommonProductServiceImpl.class);

	private Map<Product.Type, StandardProductService<Product>> serviceRegistry = Collections.emptyMap();

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

	@PostConstruct
	private void init() {
		// NOTE: all products share one cache
		// 1. clear product cache on tenant updated
		// 2. clear product cache on aircraft updated, because SalesPackageProduct are referenced on to aircraft
		registerCacheEvictOnDomainEvent(CACHE_NAME, EnumSet.of(DomainType.TENANT, DomainType.AIRCRAFT));
	}

	/**
	 * Find product with the given type and id
	 */
	@Transactional(readOnly = true)
	@Cacheable(cacheNames = CACHE_NAME, key = "#productId")
	@Override
	public Product findProduct(Type type, String productId) {
		StandardProductService<Product> service = getProductService(type);
		return service.findProduct(productId);
	}

	private StandardProductService<Product> getProductService(Type type) {
		StandardProductService<Product> service = serviceRegistry.get(type);
		if (service == null) {
			LOG.error("No StandardProductService found for product type: {}", type);
			throw new AirException(Codes.SERVICE_UNAVAILABLE, M.msg(M.SERVICE_UNAVAILABLE));
		}
		return service;
	}

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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		Map<String, StandardProductService> productServices = context.getBeansOfType(StandardProductService.class);
		ImmutableMap.Builder<Product.Type, StandardProductService<Product>> builder = ImmutableMap.builder();
		productServices.values().stream().forEach(service -> {
			ManagedProductService serviced = service.getClass().getAnnotation(ManagedProductService.class);
			if (serviced != null) {
				builder.put(serviced.value(), service);
			}
		});
		serviceRegistry = builder.build();
		LOG.debug("Product service registry: {}", serviceRegistry);
	}

}
