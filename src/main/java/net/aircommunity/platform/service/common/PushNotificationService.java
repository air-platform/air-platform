package net.aircommunity.platform.service.common;

import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.PushNotification;

/**
 * SMS service.
 * 
 * @author Xiangwen.Kong
 */
public interface PushNotificationService {

	/**
	 * Send push notification to an mobile
	 * @param pushNotificationId the push notification to be sent
	 */
	PushNotification sendNotification(String pushNotificationId);

	PushNotification sendInstantPushNotification(PushNotification pushNotification);

	PushNotification createPushNotification(PushNotification pushNotification);
	PushNotification findPushNotification(String pushNotificationId);
	PushNotification updatePushNotification(String pushNotificationId, PushNotification newPushNotification);
	Page<PushNotification> listPushNotifications(int page, int pageSize);
	void deletePushNotification(String pushNotificationId);
	void deletePushNotifications();

}
