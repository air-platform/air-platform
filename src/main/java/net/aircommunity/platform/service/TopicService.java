package net.aircommunity.platform.service;

import net.aircommunity.platform.model.Topic;
import java.util.ArrayList;

/**
 * Created by luocheng on 2017/4/17.
 */
public interface TopicService {

    ArrayList<Topic> getRecentTopics();
}
