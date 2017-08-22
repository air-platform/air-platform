package net.aircommunity.platform.service.internal.common;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.SMS;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.audience.AudienceTarget;
import cn.jpush.api.push.model.notification.Notification;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.Configuration;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.Account;
import net.aircommunity.platform.model.domain.PushNotification;
import net.aircommunity.platform.model.domain.PushNotification;
import net.aircommunity.platform.model.domain.User;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.repository.AccountRepository;
import net.aircommunity.platform.repository.PushNotificationRepository;
import net.aircommunity.platform.service.common.PushNotificationService;
import net.aircommunity.platform.service.internal.AbstractServiceSupport;
import net.aircommunity.platform.service.internal.Pages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.Date;

/**
 * SMS service implementation
 * 
 * @author Xiangwen.Kong
 */
@Service
public class PushNotificationServiceImpl  extends AbstractServiceSupport implements PushNotificationService {
	private static final Logger LOG = LoggerFactory.getLogger(PushNotificationServiceImpl.class);
    private static final String CACHE_NAME = "cache.pushnotification";
    @Resource
	private Configuration configuration;

	@Resource
	private ObjectMapper objectMapper;

    @Resource
    private PushNotificationRepository pushNotificationRepository;

    @Resource
    private AccountRepository accountRepository;

    private JPushClient jpushClient;

	@PostConstruct
	private void init() {
         //registerCacheEvictOnDomainEvent(CACHE_NAME, EnumSet.of(DomainType.TENANT));
		 jpushClient = new JPushClient(configuration.getNotificationMasterSecret(),
				configuration.getNotificationAppKey(), null, ClientConfig.getInstance());

	}


    private void doPushNotification(PushNotification pn){
        String message;
        if(pn.getType() == PushNotification.Type.PLAIN_TEXT){
            message = pn.getMessage();
        }else{
            message = pn.getRichTextLink();
        }

        PushPayload payload;
        //SMS sms = SMS.content("Test SMS", 10);

        //PushPayload payload = PushPayload.alertAll("test1", sms);
        /*PushPayload payload = PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setNotification(Notification.)
                .build();*/

        LOG.debug("push notification alias {}",pn.getAlias());
        if(Strings.isNullOrEmpty(pn.getAlias())){
            payload = PushPayload.alertAll(message);
           // PushPayload.
        }else {
            payload = PushPayload.newBuilder()
                    .setPlatform(Platform.all())
                    .setAudience(Audience.newBuilder()
                            .addAudienceTarget(AudienceTarget.alias(pn.getAlias()))
                            .build())
                    .setNotification(Notification.alert(message))
                    .build();
        }

        try {
            PushResult result = jpushClient.sendPush(payload);
            if(result.isResultOK()){
                pn.setStatus(PushNotification.Status.PUSH_SUCCESS);
                pn.setLastSendDate(new Date());
            }else{
                pn.setStatus(PushNotification.Status.PUSH_FAILED);
            }


        } catch (APIConnectionException e) {
            LOG.error("Push notification connection error, should retry later", e);
        } catch (APIRequestException e) {
            // Should review the error, and fix the request
            LOG.error("Should review the error, and fix the request", e);
            LOG.error("HTTP Status: " + e.getStatus());
            LOG.error("Error Code: " + e.getErrorCode());
            LOG.error("Error Message: " + e.getErrorMessage());
        }

    }

    @Override
    public PushNotification sendInstantPushNotification(PushNotification pushNotification)
    {

        PushNotification newPushNotification = new PushNotification();
        copyProperties(pushNotification, newPushNotification);
        newPushNotification.setCreationDate(new Date());
        newPushNotification.setStatus(PushNotification.Status.NONE_PUSH);
        doPushNotification(newPushNotification);
        return safeExecute(() -> pushNotificationRepository.save(newPushNotification), "Create PushNotification %s failed", newPushNotification);
    }

	@Override
	public PushNotification sendNotification(String pushNotificationId) {

        PushNotification pn = findPushNotification(pushNotificationId);
        doPushNotification(pn);
        PushNotification updated = safeExecute(() -> pushNotificationRepository.save(pn), "Update pushnotification %s to %s failed",
                pushNotificationId, pn);
		return pn;
	}

    @Transactional
    @Override
    public PushNotification createPushNotification(PushNotification pushNotification) {
        PushNotification newPushNotification = new PushNotification();
        copyProperties(pushNotification, newPushNotification);
        newPushNotification.setCreationDate(new Date());
        newPushNotification.setStatus(PushNotification.Status.NONE_PUSH);
        return safeExecute(() -> pushNotificationRepository.save(newPushNotification), "Create PushNotification %s failed", pushNotification);



	}

    @Cacheable(cacheNames = CACHE_NAME)
    @Override
    public PushNotification findPushNotification(String pushNotificationId) {
        PushNotification pushNotification = pushNotificationRepository.findOne(pushNotificationId);
        if (pushNotification == null) {
            LOG.error("PushNotification: {} is not found", pushNotificationId);
            throw new AirException(Codes.PUSHNOTIFICATION_NOT_FOUND, M.msg(M.PUSHNOTIFICATION_NOT_FOUND));
        }
        return pushNotification;
    }

    @Transactional
    @CachePut(cacheNames = CACHE_NAME, key = "#pushNotificationId")
    @Override
    public PushNotification updatePushNotification(String pushNotificationId, PushNotification newPushNotification) {
        PushNotification pushNotification = findPushNotification(pushNotificationId);
        copyProperties(newPushNotification, pushNotification);
        PushNotification updated = safeExecute(() -> pushNotificationRepository.save(pushNotification), "Update pushNotification %s to %s failed",
                pushNotificationId, pushNotification);
        //postDomainEvent(EVENT_UPDATE);
        return updated;
    }


    private void copyProperties(PushNotification src, PushNotification tgt) {
        tgt.setMessage(src.getMessage());
        tgt.setRichTextLink(src.getRichTextLink());
        tgt.setType(src.getType());
        tgt.setAlias(src.getAlias());
        tgt.setOwner(src.getOwner());
    }


    @Override
    public Page<PushNotification> listPushNotifications(int page, int pageSize) {
        return Pages.adapt(pushNotificationRepository.findAll(createPageRequest(page, pageSize)));
    }

    @Transactional
    @CacheEvict(cacheNames = CACHE_NAME, key = "#pushNotificationId")
    @Override
    public void deletePushNotification(String pushNotificationId) {
        safeDeletion(pushNotificationRepository, pushNotificationId, Codes.PUSHNOTIFICATION_CANNOT_BE_DELETED,
                M.msg(M.AIRCRAFT_CANNOT_BE_DELETED));
        //postDomainEvent(EVENT_DELETION);
    }

    @Transactional
    @CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
    @Override
    public void deletePushNotifications() {
        safeDeletion(pushNotificationRepository, () -> pushNotificationRepository.deleteAll(),
                Codes.PUSHNOTIFICATION_CANNOT_BE_DELETED, M.msg(M.PUSHNOTIFICATION_CANNOT_BE_DELETED));
        //postDomainEvent(EVENT_DELETION);
    }


    @Cacheable(cacheNames = CACHE_NAME)
    @Override
    public Account findAccount(String accountId) {
        Account account = accountRepository.findOne(accountId);
        if (account == null) {
            LOG.error("Failed to find account, account {} is not found", accountId);
            throw new AirException(Codes.ACCOUNT_NOT_FOUND, M.msg(M.ACCOUNT_NOT_FOUND));
        }
        return account;
    }

    @Override
    public Page<PushNotification> listUserPushNotifications(String accountId, int page, int pageSize) {
        Account account = findAccount(accountId);
        if (!User.class.isAssignableFrom(account.getClass())) {
            return Page.emptyPage(page, pageSize);
        }
        User user = User.class.cast(account);
        return Pages.adapt(pushNotificationRepository.findByOwnerOrOwnerIsNull(user, createPageRequest(page, pageSize)));
    }



}
