package net.aircommunity.platform.model.jaxb;

import javax.json.Json;
import javax.json.JsonObject;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import net.aircommunity.platform.model.Tenant;

/**
 * Adapt a Tenant object to a JsonObject presentation
 * 
 * @author Bin.Zhang
 */
public class TenantAdapter extends XmlAdapter<JsonObject, Tenant> {

	@Override
	public JsonObject marshal(Tenant tenant) throws Exception {
		return Json.createObjectBuilder().add("id", tenant.getId()).add("name", tenant.getNickName())
				.add("avatar", tenant.getAvatar()).build();
	}

	@Override
	public Tenant unmarshal(JsonObject tenant) throws Exception {
		// we don't need unmarshal
		return null;
	}

}