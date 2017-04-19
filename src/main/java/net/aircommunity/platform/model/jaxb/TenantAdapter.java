package net.aircommunity.platform.model.jaxb;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import net.aircommunity.platform.model.Tenant;

/**
 * Adapt a Tenant object to a JsonObject presentation
 * 
 * @author Bin.Zhang
 */
public class TenantAdapter extends XmlAdapter<Map<String, Object>, Tenant> {

	@Override
	public Map<String, Object> marshal(Tenant tenant) throws Exception {
		return new HashMap<String, Object>(4) {
			private static final long serialVersionUID = 1L;
			{
				put("id", tenant.getId());
				put("name", tenant.getNickName());
				put("avatar", tenant.getAvatar());
			}
		};
	}

	@Override
	public Tenant unmarshal(Map<String, Object> tenant) throws Exception {
		// we don't need unmarshal
		return null;
	}

}