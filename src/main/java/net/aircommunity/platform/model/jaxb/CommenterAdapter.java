package net.aircommunity.platform.model.jaxb;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import net.aircommunity.platform.model.domain.Account;

/**
 * Adapt a Account object to a product comment owner presentation
 * 
 * @author Bin.Zhang
 */
public class CommenterAdapter extends XmlAdapter<Map<String, Object>, Account> {

	@Override
	public Map<String, Object> marshal(Account user) throws Exception {
		return new HashMap<String, Object>(4) {
			private static final long serialVersionUID = 1L;
			{
				put("id", user.getId());
				put("name", user.getNickName());
				put("avatar", user.getAvatar());
				put("role", user.getRole().name().toLowerCase(Locale.ENGLISH));
			}
		};
	}

	@Override
	public Account unmarshal(Map<String, Object> userId) throws Exception {
		// we don't need unmarshal
		return null;
	}

}