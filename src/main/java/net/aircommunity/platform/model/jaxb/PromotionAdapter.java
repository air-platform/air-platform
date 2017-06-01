package net.aircommunity.platform.model.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import net.aircommunity.platform.model.Promotion;

/**
 * Adapt a Promotion object to a string presentation
 * 
 * @author Bin.Zhang
 */
public class PromotionAdapter extends XmlAdapter<String, Promotion> {

	@Override
	public String marshal(Promotion promotion) throws Exception {
		return promotion.getId();
	}

	@Override
	public Promotion unmarshal(String promotionId) throws Exception {
		// we don't need unmarshal
		return null;
	}

}