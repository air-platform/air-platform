package net.aircommunity.platform.service.internal.common;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.PushPayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;
import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.Configuration;
import net.aircommunity.platform.model.SmsMessage;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.service.common.NotificationService;
import net.aircommunity.platform.service.common.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import static cn.jpush.api.push.model.notification.PlatformNotification.ALERT;
import static com.taobao.api.Constants.APP_KEY;

/**
 * SMS service implementation
 * 
 * @author kxw
 */
@Service
public class NotificationServiceImpl implements NotificationService {
	private static final Logger LOG = LoggerFactory.getLogger(NotificationServiceImpl.class);

	private JPushClient jpushClient;

	@Resource
	private Configuration configuration;

	@Resource
	private ObjectMapper objectMapper;



	@PostConstruct
	private void init() {
		 jpushClient = new JPushClient(configuration.getNotificationMasterSecret(),
				configuration.getNotificationAppKey(), null, ClientConfig.getInstance());

	}

	@Override
	public void sendNotification(String message) {

		PushPayload payload = PushPayload.alertAll(message);

		try {
			PushResult result = jpushClient.sendPush(payload);
			LOG.info("Got result - " + result);

		} catch (APIConnectionException e) {
			// Connection error, should retry later
			LOG.error("Connection error, should retry later", e);

		} catch (APIRequestException e) {
			// Should review the error, and fix the request
			LOG.error("Should review the error, and fix the request", e);
			LOG.info("HTTP Status: " + e.getStatus());
			LOG.info("Error Code: " + e.getErrorCode());
			LOG.info("Error Message: " + e.getErrorMessage());
		}
	}

}
