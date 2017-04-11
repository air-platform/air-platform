package net.aircommunity.platform.model.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import net.aircommunity.platform.model.Order;

/**
 * Adapt a Order object to a string presentation
 * 
 * @author Bin.Zhang
 */
public class OrderAdapter extends XmlAdapter<String, Order> {

	@Override
	public String marshal(Order order) throws Exception {
		return order.getId();
	}

	@Override
	public Order unmarshal(String orderId) throws Exception {
		// we don't need unmarshal
		return null;
	}

}