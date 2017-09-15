package net.aircommunity.platform.model.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import net.aircommunity.platform.model.domain.VenueTemplate;

/**
 * Adapt a Account object to a string presentation
 * 
 * @author Bin.Zhang
 */
public class VenueTemplateAdapter extends XmlAdapter<String, VenueTemplate> {

	@Override
	public String marshal(VenueTemplate pd) throws Exception {
		return pd.getId();
	}

	@Override
	public VenueTemplate unmarshal(String id) throws Exception {
		// we don't need unmarshal
		return null;
	}

}