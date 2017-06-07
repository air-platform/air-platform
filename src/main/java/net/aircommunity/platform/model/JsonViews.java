package net.aircommunity.platform.model;

/**
 * JSON Views for property filtering.
 * 
 * @author Bin.Zhang
 */
public interface JsonViews {

	interface Public {
	}

	interface User extends Public {
	}

	interface Tenant extends Public {
	}

	interface Admin extends Tenant {
	}

}
