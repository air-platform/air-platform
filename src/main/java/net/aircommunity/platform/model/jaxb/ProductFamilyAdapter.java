package net.aircommunity.platform.model.jaxb;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import net.aircommunity.platform.model.domain.ProductFamily;

/**
 * Adapt a ProductFamily object to a simple id & name
 * 
 * @author Bin.Zhang
 */
public class ProductFamilyAdapter extends XmlAdapter<Map<String, Object>, ProductFamily> {

	@Override
	public Map<String, Object> marshal(ProductFamily family) throws Exception {
		return new HashMap<String, Object>(2) {
			private static final long serialVersionUID = 1L;
			{
				put("id", family.getId());
				put("name", family.getName());
			}
		};
	}

	@Override
	public ProductFamily unmarshal(Map<String, Object> family) throws Exception {
		// we don't need unmarshal
		return null;
	}

}