package net.aircommunity.platform.model.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import net.aircommunity.platform.model.domain.VenueCategory;

/**
 * Adapt a Account object to a string presentation
 * 
 * @author Bin.Zhang
 */
public class VenueCategoryAdapter extends XmlAdapter<String, VenueCategory> {

	@Override
	public String marshal(VenueCategory pd) throws Exception {
		return pd.getId();
	}

	@Override
	public VenueCategory unmarshal(String id) throws Exception {
		// we don't need unmarshal
		return null;
	}

}