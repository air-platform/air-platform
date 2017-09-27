package net.aircommunity.platform.service.internal;

import java.util.Date;
import java.util.EnumSet;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.Constants;
import net.aircommunity.platform.model.DomainEvent;
import net.aircommunity.platform.model.DomainEvent.DomainType;
import net.aircommunity.platform.model.DomainEvent.Operation;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.ActivityMessage;
import net.aircommunity.platform.model.domain.PushNotification.BusinessType;
import net.aircommunity.platform.model.domain.PushNotification.Type;
import net.aircommunity.platform.model.domain.Reviewable.ReviewStatus;
import net.aircommunity.platform.model.domain.Tenant;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.repository.ActivityMessageRepository;
import net.aircommunity.platform.service.ActivityMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ActivityMessage service implementation.
 *
 * @author Xiangwen.Kong
 */
@Service
@Transactional(readOnly = true)
public class ActivityMessageServiceImpl extends AbstractServiceSupport implements ActivityMessageService {
	private static final Logger LOG = LoggerFactory.getLogger(ActivityMessageServiceImpl.class);

	private static final String CACHE_NAME = "cache.customlandingpoint";
	// private static final String CITYSITES_INFO = "data/citysites.json";

	@Resource
	private ActivityMessageRepository activityMessageRepository;

	@PostConstruct
	private void init() {
		registerPushNotificationEvent(EnumSet.of(DomainType.ACTIVITY_MSG));
	}

	@Transactional
	@Override
	public ActivityMessage createActivityMessage(ActivityMessage activityMessage, String userName) {
		Tenant tenant = findAccount(userName, Tenant.class);
		activityMessage.setVendor(tenant);
		activityMessage.setDate(new Date());
		ActivityMessage newActivityMessage = new ActivityMessage();
		copyProperties(activityMessage, newActivityMessage);
		return safeExecute(() -> activityMessageRepository.save(newActivityMessage),
				"Create ActivityMessage %s failed", activityMessage);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public ActivityMessage findActivityMessage(String activityMessageId) {
		ActivityMessage activityMessage = activityMessageRepository.findOne(activityMessageId);
		if (activityMessage == null) {
			LOG.error("ActivityMessage {} not found", activityMessageId);
			throw new AirException(Codes.CITYSITE_NOT_FOUND, M.msg(M.CITYSITE_NOT_FOUND));
		}
		return activityMessage;
	}

	@Transactional
	@CachePut(cacheNames = CACHE_NAME, key = "#activityMessageId")
	@Override
	public ActivityMessage updateActivityMessage(String activityMessageId,
												 ActivityMessage newActivityMessage) {
		ActivityMessage activityMessage = findActivityMessage(activityMessageId);
		copyProperties(newActivityMessage, activityMessage);
		return safeExecute(() -> activityMessageRepository.save(activityMessage),
				"Update activityMessage %s to %s failed", activityMessageId, activityMessage);
	}


	@Transactional
	@CachePut(cacheNames = CACHE_NAME, key = "#activityMessageId")
	@Override
	public ActivityMessage publish(String activityMessageId, boolean isPublish) {
		ActivityMessage activityMessage = activityMessageRepository.findOne(activityMessageId);
		if (activityMessage == null) {
			LOG.error("ActivityMessage {} not found", activityMessageId);
			throw new AirException(Codes.ACTIVITY_MESSAGE_NOT_FOUND, M.msg(M.ACTIVITY_MESSAGE_NOT_FOUND));
		}
		activityMessage.setPublished(isPublish);


		if (isPublish && activityMessage.getReviewStatus() == ReviewStatus.APPROVED) {
			DomainEvent de = new DomainEvent(DomainType.ACTIVITY_MSG, Operation.PUSH_NOTIFICATION);
			de.addParam(Constants.TEMPLATE_PUSHNOTIFICATION_ACCOUNTID, "");
			String extras = new StringBuffer()
					.append(Constants.TEMPLATE_PUSHNOTIFICATION_EXTRAS_CONTENT_TYPE).append(":").append(Type.PLAIN_TEXT.toString().toLowerCase()).append(";")
					.append(Constants.TEMPLATE_PUSHNOTIFICATION_EXTRAS_BUSINESS_TYPE).append(":").append(BusinessType.ACTIVITY_MSG.toString().toLowerCase()).append(";")
					.append(Constants.TEMPLATE_PUSHNOTIFICATION_EXTRAS_ACTIVITY_MSG_ID).append(":").append(activityMessage.getId())
					.toString();
			de.addParam(Constants.TEMPLATE_PUSHNOTIFICATION_EXTRAS, extras);
			de.addParam(Constants.TEMPLATE_PUSHNOTIFICATION_MESSAGE, activityMessage.getTitle());
			postDomainEvent(de);
		}


		return safeExecute(() -> activityMessageRepository.save(activityMessage), "Publish activityMessage %s to %s failed",
				activityMessageId, activityMessage);

	}


	@Transactional
	@CachePut(cacheNames = CACHE_NAME, key = "#activityMessageId")
	@Override
	public ActivityMessage approve(String activityMessageId, ReviewStatus reviewStatus) {
		ActivityMessage activityMessage = activityMessageRepository.findOne(activityMessageId);
		if (activityMessageId == null) {
			LOG.error("activityMessageId {} not found", activityMessageId);
			throw new AirException(Codes.VENUE_TEMPLATE_NOT_FOUND, M.msg(M.VENUE_TEMPLATE_NOT_FOUND));
		}
		activityMessage.setReviewStatus(reviewStatus);
		return safeExecute(() -> activityMessageRepository.save(activityMessage), "Publish activityMessage %s to %s failed",
				activityMessageId, activityMessage);

	}


	@Transactional
	@CachePut(cacheNames = CACHE_NAME, key = "#activityMessageId")
	@Override
	public ActivityMessage disapprove(String activityMessageId, ReviewStatus reviewStatus, String reason) {
		ActivityMessage activityMessage = activityMessageRepository.findOne(activityMessageId);
		if (activityMessageId == null) {
			LOG.error("activityMessageId {} not found", activityMessageId);
			throw new AirException(Codes.VENUE_TEMPLATE_NOT_FOUND, M.msg(M.VENUE_TEMPLATE_NOT_FOUND));
		}
		activityMessage.setReviewStatus(reviewStatus);
		activityMessage.setRejectedReason(reason);

		return safeExecute(() -> activityMessageRepository.save(activityMessage), "Publish activityMessage %s to %s failed",
				activityMessageId, activityMessage);

	}

	private void copyProperties(ActivityMessage src, ActivityMessage tgt) {
		tgt.setTitle(src.getTitle());
		tgt.setThumbnails(src.getThumbnails());
		tgt.setHeadings(src.getHeadings());
		tgt.setDescription(src.getDescription());
		tgt.setVendor(src.getVendor());
		tgt.setDate(new Date());

	}

	@Override
	public Page<ActivityMessage> listActivityMessages(int page, int pageSize) {
		return Pages.adapt(activityMessageRepository.findAll(createPageRequest(page, pageSize)));
	}

	@Override
	public Page<ActivityMessage> listTenantActivityMessages(String tenantId, int page, int pageSize) {
		Tenant tenant = findAccount(tenantId, Tenant.class);
		return Pages.adapt(activityMessageRepository.findByVendor(tenant, createPageRequest(page, pageSize)));
	}

	@Override
	public Page<ActivityMessage> listPublicActivityMessages(int page, int pageSize) {
		return Pages.adapt(activityMessageRepository.findByPublishedTrue(createPageRequest(page, pageSize)));
	}

	@Transactional
	@CacheEvict(cacheNames = CACHE_NAME, key = "#activityMessageId")
	@Override
	public void deleteActivityMessage(String activityMessageId) {
		safeExecute(() -> activityMessageRepository.delete(activityMessageId),
				"Delete activityMessage %s failed", activityMessageId);
	}

	@Transactional
	@CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	@Override
	public void deleteActivityMessages() {
		safeExecute(() -> activityMessageRepository.deleteAll(), "Delete all citysites failed");
	}

}
