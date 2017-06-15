package net.aircommunity.platform.model;

/**
 * The LinkType used by {@code Banner} and {@code PromotionItem}
 * 
 * @author Bin.Zhang
 */
public enum LinkType {

	/**
	 * Link to a product (the link output should be JSON)
	 */
	PRODUCT,

	/**
	 * Link to a HTML page (the link output should be HTML page, no matter external or internal)
	 */
	CONTENT;

	public static LinkType fromString(String value) {
		for (LinkType e : values()) {
			if (e.name().equalsIgnoreCase(value)) {
				return e;
			}
		}
		return null;
	}

}
