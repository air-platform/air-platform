package net.aircommunity.platform.model.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import net.aircommunity.platform.model.Product;

/**
 * Adapt a Product object to a string presentation
 * 
 * @author Bin.Zhang
 */
public class ProductAdapter extends XmlAdapter<String, Product> {

	@Override
	public String marshal(Product product) throws Exception {
		return product.getId();
	}

	@Override
	public Product unmarshal(String productId) throws Exception {
		// we don't need unmarshal
		return null;
	}

}