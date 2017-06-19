package net.aircommunity.platform.model.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import net.aircommunity.platform.model.domain.ProductFamily;

/**
 * Adapt a ProductFamily object to a simple name
 * 
 * @author Bin.Zhang
 */
public class ProductFamilyAdapter extends XmlAdapter<String, ProductFamily> {

	@Override
	public String marshal(ProductFamily family) throws Exception {
		return family.getName();
	}

	@Override
	public ProductFamily unmarshal(String family) throws Exception {
		// we don't need unmarshal
		return null;
	}

}