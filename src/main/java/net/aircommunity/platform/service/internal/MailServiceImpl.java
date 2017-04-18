package net.aircommunity.platform.service.internal;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import net.aircommunity.platform.Configuration;
import net.aircommunity.platform.service.MailService;

/**
 * Mail service implementation
 */
@Service
public class MailServiceImpl implements MailService {
	private static final Logger LOG = LoggerFactory.getLogger(MailServiceImpl.class);

	private volatile boolean running;
	private BlockingQueue<MailItem> mailItems;
	private ExecutorService executorService;

	@Resource
	private Configuration configuration;

	@Resource
	private JavaMailSender mailSender;

	@PostConstruct
	private void init() {
		int capacity = configuration.getMailQueueSize();
		mailItems = new ArrayBlockingQueue<>(capacity <= 0 ? 1024 : capacity);
		executorService = Executors.newSingleThreadExecutor(
				new ThreadFactoryBuilder().setNameFormat("mail-pool-%d").setDaemon(true).build());
		executorService.submit(() -> {
			while (running) {
				try {
					MailItem item = mailItems.take();
					doSend(item);
				}
				catch (InterruptedException e) {
					running = false;
					Thread.currentThread().interrupt();
				}
			}
		});
	}

	private void doSend(MailItem item) {
		LOG.debug("Sending mail to {} with subject: {}, content: {}.", item.mailTo, item.subject, item.content);
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "utf-8");
			helper.setTo(item.mailTo);
			helper.setFrom(item.mailFrom);
			helper.setSubject(item.subject);
			helper.setText(item.content, true);
			mailSender.send(mimeMessage);
			LOG.debug("Mail {} sent successfully", item);
		}
		catch (Exception e) {
			LOG.error(String.format("Failed to send email to: %s,  cause: %s", item.mailTo, e.getMessage()), e);
		}
	}

	@PreDestroy
	private void shutdown() {
		running = false;
		executorService.shutdownNow();
	}

	@Override
	public void sendMail(String email, String subject, String content) {
		try {
			MailItem item = new MailItem(email, configuration.getMailFrom(), subject, content);
			mailItems.put(item);
			LOG.debug("Mail {} queued, will be send later", item);
		}
		catch (InterruptedException e) {
			running = false;
			Thread.currentThread().interrupt();
		}
	}

	private static class MailItem {
		final String mailTo;
		final String mailFrom;
		final String subject;
		final String content;

		MailItem(String mailTo, String mailFrom, String subject, String content) {
			this.mailTo = mailTo;
			this.mailFrom = mailFrom;
			this.subject = subject;
			this.content = content;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("MailItem [mailTo=").append(mailTo).append(", mailFrom=").append(mailFrom)
					.append(", subject=").append(subject).append(", content=").append(content).append("]");
			return builder.toString();
		}
	}

}
