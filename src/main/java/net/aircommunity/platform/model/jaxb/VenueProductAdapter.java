package net.aircommunity.platform.model.jaxb;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.model.domain.VenueCategory;

/**
 * Adapt a Account object to a string presentation
 * 
 * @author Xiangwen.kong
 */
public class VenueProductAdapter extends XmlAdapter<Map<String, Object>,  Product> {

	@Override
	public Map<String, Object> marshal(Product product) throws Exception {
		return new HashMap<String, Object>() {
			private static final long serialVersionUID = 1L;
			{
				put("id", product.getId());
				put("name", product.getName());
				put("type", product.getType());
				put("category", product.getCategory());
				put("image", product.getImage());
				put("imageSmall", product.getImageSmall());
				put("stock", product.getStock());
				put("originalPrice", product.getOriginalPrice());
				put("specialPrice", product.getSpecialPrice());
				put("score", product.getScore());
				put("rank", product.getRank());
			}
		};
	}

	@Override
	public Product unmarshal(Map<String, Object> productId) throws Exception {
		// we don't need unmarshal
		return null;
	}

}