package net.aircommunity.platform.common.ua;

import java.util.Map;

/**
 * Device parsed data class
 *
 * @author Steve Jiang (@sjiang) <gh at iamsteve com>
 */
public class Device {
	public final String family;

	public Device(String family) {
		this.family = family;
	}

	public static Device fromMap(Map<String, String> m) {
		return new Device((String) m.get("family"));
	}

	@Override
	public boolean equals(Object other) {
		if (other == this)
			return true;
		if (!(other instanceof Device))
			return false;

		Device o = (Device) other;
		return (this.family != null && this.family.equals(o.family)) || this.family == o.family;
	}

	@Override
	public int hashCode() {
		return family == null ? 0 : family.hashCode();
	}

	@Override
	public String toString() {
		return String.format("{\"family\": %s}", family == null ? Constants.EMPTY_STRING : '"' + family + '"');
	}
}