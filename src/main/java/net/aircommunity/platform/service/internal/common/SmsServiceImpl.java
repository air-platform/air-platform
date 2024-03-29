package net.aircommunity.platform.service.internal.common;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
import net.aircommunity.platform.service.common.SmsService;

/**
 * SMS service implementation
 * 
 * @author kxw
 */
@Service
public class SmsServiceImpl implements SmsService {
	private static final Logger LOG = LoggerFactory.getLogger(SmsServiceImpl.class);

	private TaobaoClient client;

	@Resource
	private Configuration configuration;

	@Resource
	private ObjectMapper objectMapper;

	@PostConstruct
	private void init() {
		client = new DefaultTaobaoClient(configuration.getSmsUrl(), configuration.getSmsAppKey(),
				configuration.getSmsAppSecret());
	}

	@Override
	public void sendSms(String mobile, String message) {
		AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
		req.setExtend(configuration.getSmsExtend());
		req.setSmsType(configuration.getSmsType());
		req.setSmsFreeSignName(configuration.getSmsSign());
		req.setSmsTemplateCode(configuration.getSmsTmplCode());
		req.setRecNum(mobile);

		SmsMessage smsMsg = new SmsMessage();
		smsMsg.setCode(message);
		try {
			String body = objectMapper.writeValueAsString(smsMsg);
			LOG.debug("Get SMS message body:{}", body);
			req.setSmsParamString(body);
			AlibabaAliqinFcSmsNumSendResponse rsp = client.execute(req);
			LOG.debug("Get SMS response:{}.", rsp.getBody());
		}
		catch (Exception e) {
			LOG.error("Failed to send SMS:" + e.getMessage(), e);
			throw new AirException(Codes.SERVICE_UNAVAILABLE, M.msg(M.SMS_SEND_FAILURE));
		}
	}

}
