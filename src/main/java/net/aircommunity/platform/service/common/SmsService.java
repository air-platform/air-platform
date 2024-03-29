package net.aircommunity.platform.service.common;

/**
 * SMS service.
 * 
 * @author Bin.Zhang
 */
public interface SmsService {

	/**
	 * Send SMS to an mobile
	 * 
	 * @param mobile the mobile to receive the message
	 * @param message the SMS message to be sent
	 * @throws AirException if failed to send
	 */
	void sendSms(String mobile, String message);

}
