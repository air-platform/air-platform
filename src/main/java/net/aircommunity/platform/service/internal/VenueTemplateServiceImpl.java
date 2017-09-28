package net.aircommunity.platform.service.internal;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.User;
import net.aircommunity.platform.model.domain.VenueCategory;
import net.aircommunity.platform.model.domain.VenueInfo;
import net.aircommunity.platform.model.domain.VenueTemplate;
import net.aircommunity.platform.model.domain.VenueTemplateCouponUser;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.repository.VenueTemplateRepository;
import net.aircommunity.platform.service.VenueTemplateService;
import net.aircommunity.platform.service.security.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * VenueTemplate service implementation.
 *
 * @author Xiangwen.Kong
 */
@Service
@Transactional(readOnly = true)
public class VenueTemplateServiceImpl extends AbstractServiceSupport implements VenueTemplateService {
	private static final Logger LOG = LoggerFactory.getLogger(VenueTemplateServiceImpl.class);

	private static final String CACHE_NAME = "cache.venuetemplate";
	@Resource
	private VenueTemplateRepository venueTemplateRepository;

	@Resource
	private AccountService accountService;

	@Transactional
	@Override
	public VenueTemplate createVenueTemplate(VenueTemplate venueTemplate) {
		VenueTemplate newVenueTemplate = new VenueTemplate();
		copyProperties(venueTemplate, newVenueTemplate);
		return safeExecute(() -> venueTemplateRepository.save(newVenueTemplate), "Create VenueTemplate %s failed",
				venueTemplate);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public VenueTemplate findVenueTemplate(String venueTemplateId) {
		VenueTemplate venueTemplate = venueTemplateRepository.findOne(venueTemplateId);
		if (venueTemplate == null) {
			LOG.error("VenueTemplate {} not found", venueTemplateId);
			throw new AirException(Codes.VENUE_TEMPLATE_NOT_FOUND, M.msg(M.VENUE_TEMPLATE_NOT_FOUND));
		}

		List<VenueInfo> depdupe = new ArrayList<>(new LinkedHashSet<>(venueTemplate.getVenueInfos()));
		venueTemplate.setVenueInfos(depdupe);

		venueTemplate.getVenueInfos().stream().forEach(info -> {
			List<VenueCategory> dep = new ArrayList<>(new LinkedHashSet<>(info.getVenueCategories()));
			info.setVenueCategories(dep);
		});


		return venueTemplate;
	}

	@Transactional
	@Override
	@CacheEvict(cacheNames = CACHE_NAME, key = "#venueTemplateId")
	public VenueTemplateCouponUser grabCoupon(String venueTemplateId, String userName) {
		VenueTemplate venueTemplate = venueTemplateRepository.findOne(venueTemplateId);
		Set<VenueTemplateCouponUser> all = null;
		List<VenueTemplateCouponUser> tu = null;
		if (venueTemplate == null) {
			LOG.error("VenueTemplate {} not found", venueTemplateId);
			throw new AirException(Codes.VENUE_TEMPLATE_NOT_FOUND, M.msg(M.VENUE_TEMPLATE_NOT_FOUND));
		}
		// accountService.findAccount().

		all = venueTemplate.getVenueTemplateCouponUsers();
		if (all != null && !all.isEmpty()) {
			tu = all.stream().filter(s -> s.getUser().getId().equals(userName))
					.collect(Collectors.toList());
			if (tu != null && !tu.isEmpty()) {
				throw new AirException(Codes.VENUE_TEMPLATE_HAS_GRABBED_COUPON, M.msg(M.VENUE_TEMPLATE_HAS_GRABBED_COUPON));
			}
		}

		if (venueTemplate.getCouponExpiredDate() != null && venueTemplate.getCouponExpiredDate().before(new Date())) {
			throw new AirException(Codes.VENUE_TEMPLATE_COUPON_EXPIRED, M.msg(M.VENUE_TEMPLATE_COUPON_EXPIRED));
		}

		int remain = venueTemplate.getCouponRemainNum();
		if (remain > 0) {

			/*
			 * 1. add user-coupon field
			 * 2. remaining coupon num subtracts 1
			 * 3. add user points
			 * 3. return one user-coupon required by app
			 *
			 */
			User user = findAccount(userName, User.class);
			Set<VenueTemplateCouponUser> allCouponUser = new HashSet<VenueTemplateCouponUser>();

			venueTemplate.setCouponRemainNum(remain - 1);
			VenueTemplateCouponUser newCouponUser = new VenueTemplateCouponUser();
			newCouponUser.setUser(user);
			newCouponUser.setUserId(userName);
			newCouponUser.setVenueTemplate(venueTemplate);
			newCouponUser.setCouponNum(1);
			newCouponUser.setPointsPerCoupon(venueTemplate.getPointsPerCoupon());

			allCouponUser.addAll(venueTemplate.getVenueTemplateCouponUsers());
			allCouponUser.add(newCouponUser);
			venueTemplate.setVenueTemplateCouponUsers(allCouponUser);
			VenueTemplate vt = safeExecute(() -> venueTemplateRepository.save(venueTemplate), "Publish venueTemplate %s to %s failed",
					venueTemplateId, venueTemplate);

			accountService.updateUserPoints(userName, venueTemplate.getPointsPerCoupon());
			//updateVenueTemplate(venueTemplateId, venueTemplate);
			//VenueTemplate vt = findVenueTemplate(venueTemplateId);

			all = vt.getVenueTemplateCouponUsers();
			if (all != null && !all.isEmpty()) {
				tu = all.stream()
						.filter(cu -> cu.getUser().getId().equals(userName))
						.collect(Collectors.toList());
				tu.stream().forEach(cu -> cu.setUserId(cu.getUser().getId()));
			}
		}
		else {
			throw new AirException(Codes.VENUE_TEMPLATE_NO_COUPON, M.msg(M.VENUE_TEMPLATE_NO_COUPON));

		}

		if (tu != null && !tu.isEmpty()) {
			return tu.get(0);
		}
		else {
			return null;
		}
	}

	@Transactional
	@CachePut(cacheNames = CACHE_NAME, key = "#venueTemplateId")
	@Override
	public VenueTemplate publish(String venueTemplateId, boolean isPublish) {
		VenueTemplate venueTemplate = venueTemplateRepository.findOne(venueTemplateId);
		if (venueTemplate == null) {
			LOG.error("VenueTemplate {} not found", venueTemplateId);
			throw new AirException(Codes.VENUE_TEMPLATE_NOT_FOUND, M.msg(M.VENUE_TEMPLATE_NOT_FOUND));
		}
		venueTemplate.setPublished(isPublish);
		return safeExecute(() -> venueTemplateRepository.save(venueTemplate), "Publish venueTemplate %s to %s failed",
				venueTemplateId, venueTemplate);

	}

	@Transactional
	@CachePut(cacheNames = CACHE_NAME, key = "#venueTemplateId")
	@Override
	public VenueTemplate updateVenueTemplate(String venueTemplateId, VenueTemplate newVenueTemplate) {
		VenueTemplate venueTemplate = findVenueTemplate(venueTemplateId);
		copyProperties(newVenueTemplate, venueTemplate);
		return safeExecute(() -> venueTemplateRepository.save(venueTemplate), "Update venueTemplate %s to %s failed",
				venueTemplateId, venueTemplate);
	}

	private void copyProperties(VenueTemplate src, VenueTemplate tgt) {

		tgt.setName(src.getName());
		tgt.setBackgroundColor(src.getBackgroundColor());
		tgt.setBackgroundPic(src.getBackgroundPic());
		tgt.setDescription(src.getDescription());
		tgt.setPublished(src.isPublished());
		tgt.setCouponTotalNum(src.getCouponTotalNum());
		tgt.setCouponRemainNum(src.getCouponRemainNum());
		tgt.setPointsPerCoupon(src.getPointsPerCoupon());
		tgt.setCouponExpiredDate(src.getCouponExpiredDate());
		tgt.setVenueInfos(src.getVenueInfos());
		tgt.setVenueTemplateCouponUsers(src.getVenueTemplateCouponUsers());

	}

	@Override
	public Page<VenueTemplate> listVenueTemplates(int page, int pageSize) {
		return Pages.adapt(venueTemplateRepository.findAll(createPageRequest(page, pageSize)));
	}

	@Override
	public List<VenueTemplate> listPublicVenueTemplates() {
		return venueTemplateRepository.findByPublishedTrue();
	}

	@Transactional
	@CacheEvict(cacheNames = CACHE_NAME, key = "#venueTemplateId")
	@Override
	public void deleteVenueTemplate(String venueTemplateId) {
		// safeExecute(() -> venueTemplateRepository.delete(venueTemplateId), "Delete venueTemplate %s failed",
		// venueTemplateId);
		safeDeletion(venueTemplateRepository, venueTemplateId, Codes.VENUE_TEMPLATE_CANNOT_BE_DELETED,
				M.msg(M.VENUE_TEMPLATE_CANNOT_BE_DELETED));

	}

	@Transactional
	@CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	@Override
	public void deleteVenueTemplates() {
		safeExecute(() -> venueTemplateRepository.deleteAll(), "Delete all citysites failed");
	}

}
