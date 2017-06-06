package net.aircommunity.platform.service;

import java.util.List;

import javax.annotation.Nonnull;

import net.aircommunity.platform.model.AirqTopic;

/**
 * AirQ Topic Service
 * 
 * @author luocheng
 */
public interface AirqTopicService {

	/**
	 * List all recent topics.
	 * 
	 * @return a list of topics or empty if none
	 */
	@Nonnull
	List<AirqTopic> listRecentTopics();
}
