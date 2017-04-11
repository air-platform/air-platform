package net.aircommunity.platform.service.internal;

import net.aircommunity.platform.Configuration;
import net.aircommunity.platform.rest.AccountResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import net.aircommunity.platform.service.MailService;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * TODO
 */
@Service
public class MailServiceImpl implements MailService {
	private static final Logger LOG = LoggerFactory.getLogger(AccountResource.class);

	@Autowired
	private JavaMailSender mailSender;

	@Resource
	private Configuration configuration;

	@Override
	public void sendMail(String email, String subject, String content) {

			LOG.debug("Sending mail {}, content {}.", email, content);
			/*SimpleMailMessage message = new SimpleMailMessage();

			message.setFrom(configuration.getMailFrom());
			message.setTo(email);
			message.setSubject(subject);
			message.setText(content);
			mailSender.send(message);*/


		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = null;
		try {
			//helper = new MimeMessageHelper(mimeMessage, false, "utf-8");
			helper = new MimeMessageHelper(mimeMessage, true);
			//mimeMessage.setContent(content, "text/html");
			helper.setText(content, true);
			helper.setTo(email);
			helper.setSubject(subject);
			helper.setFrom(configuration.getMailFrom());
			mailSender.send(mimeMessage);
		} catch (MessagingException e) {
			e.printStackTrace();
		}


	}

}
