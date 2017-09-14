package net.aircommunity.platform.model.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import net.aircommunity.platform.model.domain.VenueCategory;
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
	public VenueTemplate unmarshal(String id) throws Exception {
		// we don't need unmarshal
		return null;
	}

}