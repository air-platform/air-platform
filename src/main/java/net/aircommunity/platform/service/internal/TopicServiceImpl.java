package net.aircommunity.platform.service.internal;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.Configuration;
import net.aircommunity.platform.model.Topic;
import net.aircommunity.platform.service.TopicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by luocheng on 2017/4/17.
 */
@Service
public class TopicServiceImpl implements TopicService{

    private static final Logger LOG = LoggerFactory.getLogger(MailServiceImpl.class);

    @Resource
    private Configuration configuration;

    public ArrayList<Topic> getRecentTopics() {

        ArrayList<Topic> topics = new ArrayList<>();

        try {
            URL url = new URL(configuration.getNodebbUrl() + "/api/recent");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");

            int respCode = conn.getResponseCode();
            if (respCode != HttpURLConnection.HTTP_OK) {
                LOG.debug("Get NodeBB recent topics Failed : HTTP error code : " + respCode);
            }

            JsonReader jsonReader = Json.createReader(conn.getInputStream());
            JsonObject jsonObj = jsonReader.readObject();

            JsonArray topicsArray = jsonObj.getJsonArray("topics");
            for (int i = 0; i < topicsArray.size(); i++) {
                JsonObject t = topicsArray.getJsonObject(i);

                String title = t.getString("title");
                int tid = t.getInt("tid");
                String urlTopic = configuration.getNodebbUrl() + "/topic/" + tid;
                Topic topic = new Topic();
                topic.setTitle(title);
                topic.setUrl(urlTopic);
                topics.add(topic);
            }
            jsonReader.close();
            conn.disconnect();
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new AirException(Codes.INTERNAL_ERROR, "Failed to get NodeBB recent topics:" + e.getMessage(), e);
        }
        return topics;
    }
}
