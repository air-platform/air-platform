package net.aircommunity.platform.model;

/**
 * Currency Unit.
 *
 * @author Bin.Zhang
 */
public enum CurrencyUnit {
	// XXX use CNY instead of RMB ?
	RMB, CNY, HKD, USD, EUR;

	public static CurrencyUnit fromString(String value) {
		for (CurrencyUnit e : values()) {
			if (e.name().equalsIgnoreCase(value)) {
				return e;
			}
		}
		return null;
	}
}
