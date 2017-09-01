package net.aircommunity.platform.model.jaxb;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import net.aircommunity.platform.model.domain.Account;
import net.aircommunity.platform.model.domain.VenueCategory;
import net.aircommunity.platform.model.domain.VenueCategoryProduct;

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
		/*public Map<String, Object> marshal(VenueCategory pd) throws Exception {
			//return pd.getId();

			return new HashMap<String, Object>(4) {
				private static final long serialVersionUID = 1L;
				{
					put("id", pd.getId());
					put("name", pd.getName());
					put("description", pd.getDescription());
				}
			};
		}*/

	@Override
	public VenueCategory unmarshal(String id) throws Exception {
		// we don't need unmarshal
		return null;
	}

}