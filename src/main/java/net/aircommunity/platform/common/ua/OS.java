package net.aircommunity.platform.common.ua;

import java.util.Map;

/**
 * Operating System parsed data
 */
public class OS {
	public static final OS OTHER = new OS("Other", null, null, null, null);

	public final String family;
	public final String major;
	public final String minor;
	public final String patch;
	public final String patchMinor;

	public OS(String family, String major, String minor, String patch, String patchMinor) {
		this.family = family;
		this.major = major;
		this.minor = minor;
		this.patch = patch;
		this.patchMinor = patchMinor;
	}

	public static OS fromMap(Map<String, String> m) {
		return new OS(m.get("family"), m.get("major"), m.get("minor"), m.get("patch"), m.get("patch_minor"));
	}

	@Override
	public boolean equals(Object other) {
		if (other == this)
			return true;
		if (!(other instanceof OS))
			return false;

		OS o = (OS) other;
		return ((this.family != null && this.family.equals(o.family)) || this.family == o.family)
				&& ((this.major != null && this.major.equals(o.major)) || this.major == o.major)
				&& ((this.minor != null && this.minor.equals(o.minor)) || this.minor == o.minor)
				&& ((this.patch != null && this.patch.equals(o.patch)) || this.patch == o.patch)
				&& ((this.patchMinor != null && this.patchMinor.equals(o.patchMinor))
						|| this.patchMinor == o.patchMinor);
	}

	@Override
	public int hashCode() {
		int h = family == null ? 0 : family.hashCode();
		h += major == null ? 0 : major.hashCode();
		h += minor == null ? 0 : minor.hashCode();
		h += patch == null ? 0 : patch.hashCode();
		h += patchMinor == null ? 0 : patchMinor.hashCode();
		return h;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OS [family=").append(family).append(", major=").append(major).append(", minor=").append(minor)
				.append(", patch=").append(patch).append(", patchMinor=").append(patchMinor).append("]");
		return builder.toString();
	}
}