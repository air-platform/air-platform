package net.aircommunity.platform.service;

/**
 * Email Service
 * 
 * @author Bin.Zhang
 */
public interface MailService {

	/**
	 * Send email.
	 * 
	 * @param email the email to receive the message
	 * @param content the content to be sent
	 * @throws AirException if failed to send
	 */
	void sendMail(String email, String content);

}
