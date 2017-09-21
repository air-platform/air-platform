package net.aircommunity.platform.model.jaxb;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import net.aircommunity.platform.model.domain.User;
import net.aircommunity.platform.model.domain.VenueInfo;

/**
 * Adapt a Account object to a string presentation
 * 
 * @author Bin.Zhang
 */
public class UserAdapter extends XmlAdapter<Map<String, Object>, User> {

	@Override
	public Map<String, Object> marshal(User user) throws Exception {
		return new HashMap<String, Object>() {
			private static final long serialVersionUID = 1L;
			{
				put("id", user.getId());
				put("nickName", user.getNickName());
			}
		};
	}

	@Override
	public User unmarshal(Map<String, Object> productId) throws Exception {
		// we don't need unmarshal
		return null;
	}

}