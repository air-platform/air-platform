package net.aircommunity.platform.model.jaxb;

import javax.annotation.Resource;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import net.aircommunity.platform.model.domain.VenueCategory;
import net.aircommunity.platform.model.domain.VenueInfo;
import net.aircommunity.platform.service.VenueInfoService;

/**
 * Adapt a Account object to a string presentation
 * 
 * @author Bin.Zhang
 */
public class VenueInfoAdapter extends XmlAdapter<String, VenueInfo> {

	@Override
	public String marshal(VenueInfo pd) throws Exception {
		return pd.getId();
	}

	@Override
	public VenueInfo unmarshal(String id) throws Exception {
		// we don't need unmarshal
		return new VenueInfo(id);
	}

}