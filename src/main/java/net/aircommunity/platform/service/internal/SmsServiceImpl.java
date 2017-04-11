package net.aircommunity.platform.service.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;
import net.aircommunity.platform.Configuration;
import net.aircommunity.platform.model.SmsMessage;
import net.aircommunity.platform.rest.AccountResource;
import net.aircommunity.rest.support.ObjectMappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import net.aircommunity.platform.service.SmsService;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * TODO
 */
@Service
public class SmsServiceImpl implements SmsService {

	private static final Logger LOG = LoggerFactory.getLogger(AccountResource.class);

	private TaobaoClient client;
	private ObjectMapper objectMapper;
	private SmsMessage smsMsg;
	@Resource
	private Configuration configuration;


	@Bean
	@Primary
	public ObjectMapper objectMapper() {
		return ObjectMappers.getObjectMapper();
	}


	@PostConstruct
	private void init(){
		client = new DefaultTaobaoClient(configuration.getSmsUrl()
				, configuration.getSmsAppKey()
				, configuration.getSmsAppSecret());

		objectMapper = objectMapper();
		smsMsg = new SmsMessage();

	}
	@Override
	public void sendSms(String mobile, String message) {


		AlibabaAliqinFcSmsNumSendResponse rsp = null;

		AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
		req.setExtend(configuration.getSmsExtend());
		req.setSmsType(configuration.getSmsType());
		req.setSmsFreeSignName(configuration.getSmsSign());//configuration.getSmsSign()
		req.setSmsTemplateCode(configuration.getSmsTmplCode());
		req.setRecNum(mobile);

		smsMsg.setMsgtype(message);

		try {
			String body = objectMapper.writeValueAsString(smsMsg);
			LOG.debug("Get sms message body:{}", body);
			req.setSmsParamString(body);
			rsp = client.execute(req);
			LOG.debug("Get sms resp:{}.", rsp.getBody());

		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (ApiException e) {
			e.printStackTrace();
		}








	}

}
